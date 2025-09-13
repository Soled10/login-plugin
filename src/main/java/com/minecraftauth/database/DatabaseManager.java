package com.minecraftauth.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DatabaseManager {
    private HikariDataSource dataSource;
    private final FileConfiguration config;

    public DatabaseManager(FileConfiguration config) {
        this.config = config;
        initializeDatabase();
    }

    private void initializeDatabase() {
        HikariConfig hikariConfig = new HikariConfig();
        
        // Configuração do SQLite
        String dbPath = config.getString("database.path", "plugins/MinecraftAuth/users.db");
        hikariConfig.setJdbcUrl("jdbc:sqlite:" + dbPath);
        hikariConfig.setDriverClassName("org.sqlite.JDBC");
        
        // Configurações de pool de conexões
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setIdleTimeout(600000);
        hikariConfig.setMaxLifetime(1800000);
        
        dataSource = new HikariDataSource(hikariConfig);
        
        // Criar tabelas
        createTables();
    }

    private void createTables() {
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                uuid VARCHAR(36) PRIMARY KEY,
                username VARCHAR(16) NOT NULL UNIQUE,
                password_hash VARCHAR(255) NOT NULL,
                is_original BOOLEAN DEFAULT FALSE,
                registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                last_login TIMESTAMP,
                login_attempts INTEGER DEFAULT 0,
                locked_until TIMESTAMP NULL
            )
        """;

        String createSessionsTable = """
            CREATE TABLE IF NOT EXISTS sessions (
                uuid VARCHAR(36) PRIMARY KEY,
                session_token VARCHAR(255) NOT NULL,
                expires_at TIMESTAMP NOT NULL,
                FOREIGN KEY (uuid) REFERENCES users(uuid) ON DELETE CASCADE
            )
        """;

        String createOriginalAccountsTable = """
            CREATE TABLE IF NOT EXISTS original_accounts (
                username VARCHAR(16) PRIMARY KEY,
                uuid VARCHAR(36) NOT NULL,
                added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        try (Connection conn = dataSource.getConnection()) {
            conn.createStatement().execute(createUsersTable);
            conn.createStatement().execute(createSessionsTable);
            conn.createStatement().execute(createOriginalAccountsTable);
            
            Bukkit.getLogger().info("[MinecraftAuth] Tabelas do banco de dados criadas com sucesso!");
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[MinecraftAuth] Erro ao criar tabelas: " + e.getMessage());
        }
    }

    public CompletableFuture<Boolean> registerUser(UUID uuid, String username, String passwordHash, boolean isOriginal) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "INSERT INTO users (uuid, username, password_hash, is_original) VALUES (?, ?, ?, ?)";
            
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, uuid.toString());
                stmt.setString(2, username);
                stmt.setString(3, passwordHash);
                stmt.setBoolean(4, isOriginal);
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
                
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[MinecraftAuth] Erro ao registrar usuário: " + e.getMessage());
                return false;
            }
        });
    }

    public CompletableFuture<UserData> getUserData(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT * FROM users WHERE uuid = ?";
            
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, uuid.toString());
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return new UserData(
                        UUID.fromString(rs.getString("uuid")),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getBoolean("is_original"),
                        rs.getTimestamp("registered_at"),
                        rs.getTimestamp("last_login"),
                        rs.getInt("login_attempts"),
                        rs.getTimestamp("locked_until")
                    );
                }
                
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[MinecraftAuth] Erro ao buscar dados do usuário: " + e.getMessage());
            }
            
            return null;
        });
    }

    public CompletableFuture<UserData> getUserDataByUsername(String username) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT * FROM users WHERE username = ?";
            
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return new UserData(
                        UUID.fromString(rs.getString("uuid")),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getBoolean("is_original"),
                        rs.getTimestamp("registered_at"),
                        rs.getTimestamp("last_login"),
                        rs.getInt("login_attempts"),
                        rs.getTimestamp("locked_until")
                    );
                }
                
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[MinecraftAuth] Erro ao buscar dados do usuário por username: " + e.getMessage());
            }
            
            return null;
        });
    }

    public CompletableFuture<Boolean> updatePassword(UUID uuid, String newPasswordHash) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "UPDATE users SET password_hash = ? WHERE uuid = ?";
            
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, newPasswordHash);
                stmt.setString(2, uuid.toString());
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
                
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[MinecraftAuth] Erro ao atualizar senha: " + e.getMessage());
                return false;
            }
        });
    }

    public CompletableFuture<Boolean> updateLastLogin(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP, login_attempts = 0 WHERE uuid = ?";
            
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, uuid.toString());
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
                
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[MinecraftAuth] Erro ao atualizar último login: " + e.getMessage());
                return false;
            }
        });
    }

    public CompletableFuture<Boolean> incrementLoginAttempts(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "UPDATE users SET login_attempts = login_attempts + 1 WHERE uuid = ?";
            
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, uuid.toString());
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
                
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[MinecraftAuth] Erro ao incrementar tentativas de login: " + e.getMessage());
                return false;
            }
        });
    }

    public CompletableFuture<Boolean> lockAccount(UUID uuid, long lockDurationMinutes) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "UPDATE users SET locked_until = datetime('now', '+" + lockDurationMinutes + " minutes') WHERE uuid = ?";
            
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, uuid.toString());
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
                
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[MinecraftAuth] Erro ao bloquear conta: " + e.getMessage());
                return false;
            }
        });
    }

    public CompletableFuture<Boolean> addOriginalAccount(String username, UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "INSERT OR REPLACE INTO original_accounts (username, uuid) VALUES (?, ?)";
            
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, username);
                stmt.setString(2, uuid.toString());
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
                
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[MinecraftAuth] Erro ao adicionar conta original: " + e.getMessage());
                return false;
            }
        });
    }

    public CompletableFuture<Boolean> isOriginalAccount(String username) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT COUNT(*) FROM original_accounts WHERE username = ?";
            
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[MinecraftAuth] Erro ao verificar conta original: " + e.getMessage());
            }
            
            return false;
        });
    }

    public CompletableFuture<Boolean> isOriginalAccount(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT COUNT(*) FROM original_accounts WHERE uuid = ?";
            
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, uuid.toString());
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[MinecraftAuth] Erro ao verificar conta original por UUID: " + e.getMessage());
            }
            
            return false;
        });
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}