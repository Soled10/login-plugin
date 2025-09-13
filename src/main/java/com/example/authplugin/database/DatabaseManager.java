package com.example.authplugin.database;

import com.example.authplugin.AuthPlugin;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseManager {
    
    private final AuthPlugin plugin;
    private Connection connection;
    private final String databasePath;
    
    public DatabaseManager(AuthPlugin plugin) {
        this.plugin = plugin;
        this.databasePath = plugin.getConfigManager().getDatabasePath();
    }
    
    public boolean initialize() {
        try {
            // Criar diretório se não existir
            File dbFile = new File(databasePath);
            File parentDir = dbFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            // Conectar ao banco de dados SQLite
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
            
            // Criar tabelas
            createTables();
            
            plugin.getLogger().info("Banco de dados inicializado com sucesso!");
            return true;
            
        } catch (ClassNotFoundException | SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao inicializar banco de dados:", e);
            return false;
        }
    }
    
    private void createTables() throws SQLException {
        String createPlayersTable = "CREATE TABLE IF NOT EXISTS players (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "uuid TEXT, " +
                "premium BOOLEAN DEFAULT FALSE, " +
                "last_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "last_ip TEXT, " +
                "login_attempts INTEGER DEFAULT 0" +
                ")";
        
        String createSessionsTable = "CREATE TABLE IF NOT EXISTS sessions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, " +
                "uuid TEXT, " +
                "ip_address TEXT, " +
                "login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "expires_at TIMESTAMP, " +
                "FOREIGN KEY (username) REFERENCES players(username) ON DELETE CASCADE" +
                ")";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createPlayersTable);
            stmt.execute(createSessionsTable);
            
            // Criar índices para performance
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_username ON players(username)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_uuid ON players(uuid)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_sessions_username ON sessions(username)");
        }
    }
    
    public boolean registerPlayer(String username, String password, UUID uuid, boolean isPremium, String ip) {
        String sql = "INSERT INTO players (username, password, uuid, premium, last_ip) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username.toLowerCase());
            stmt.setString(2, BCrypt.hashpw(password, BCrypt.gensalt()));
            stmt.setString(3, uuid != null ? uuid.toString() : null);
            stmt.setBoolean(4, isPremium);
            stmt.setString(5, ip);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Erro ao registrar jogador " + username + ":", e);
            return false;
        }
    }
    
    public boolean isPlayerRegistered(String username) {
        String sql = "SELECT COUNT(*) FROM players WHERE LOWER(username) = LOWER(?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Erro ao verificar registro do jogador " + username + ":", e);
            return false;
        }
    }
    
    public boolean verifyPassword(String username, String password) {
        String sql = "SELECT password FROM players WHERE LOWER(username) = LOWER(?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    return BCrypt.checkpw(password, hashedPassword);
                }
            }
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Erro ao verificar senha do jogador " + username + ":", e);
        }
        
        return false;
    }
    
    public boolean changePassword(String username, String newPassword) {
        String sql = "UPDATE players SET password = ? WHERE LOWER(username) = LOWER(?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
            stmt.setString(2, username);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Erro ao alterar senha do jogador " + username + ":", e);
            return false;
        }
    }
    
    public boolean isPremiumPlayer(String username) {
        String sql = "SELECT premium FROM players WHERE LOWER(username) = LOWER(?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getBoolean("premium");
            }
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Erro ao verificar status premium do jogador " + username + ":", e);
            return false;
        }
    }
    
    public void updateLastLogin(String username, String ip) {
        String sql = "UPDATE players SET last_login = CURRENT_TIMESTAMP, last_ip = ?, login_attempts = 0 WHERE LOWER(username) = LOWER(?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, ip);
            stmt.setString(2, username);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Erro ao atualizar último login do jogador " + username + ":", e);
        }
    }
    
    public int getLoginAttempts(String username) {
        String sql = "SELECT login_attempts FROM players WHERE LOWER(username) = LOWER(?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("login_attempts") : 0;
            }
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Erro ao obter tentativas de login do jogador " + username + ":", e);
            return 0;
        }
    }
    
    public void incrementLoginAttempts(String username) {
        String sql = "UPDATE players SET login_attempts = login_attempts + 1 WHERE LOWER(username) = LOWER(?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Erro ao incrementar tentativas de login do jogador " + username + ":", e);
        }
    }
    
    public void resetLoginAttempts(String username) {
        String sql = "UPDATE players SET login_attempts = 0 WHERE LOWER(username) = LOWER(?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Erro ao resetar tentativas de login do jogador " + username + ":", e);
        }
    }
    
    public boolean createSession(String username, UUID uuid, String ip, long expiresAt) {
        // Remover sessões antigas primeiro
        removeSession(username);
        
        String sql = "INSERT INTO sessions (username, uuid, ip_address, expires_at) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username.toLowerCase());
            stmt.setString(2, uuid != null ? uuid.toString() : null);
            stmt.setString(3, ip);
            stmt.setTimestamp(4, new Timestamp(expiresAt));
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Erro ao criar sessão para jogador " + username + ":", e);
            return false;
        }
    }
    
    public boolean hasValidSession(String username, String ip) {
        String sql = "SELECT COUNT(*) FROM sessions WHERE LOWER(username) = LOWER(?) AND ip_address = ? AND expires_at > CURRENT_TIMESTAMP";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, ip);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Erro ao verificar sessão do jogador " + username + ":", e);
            return false;
        }
    }
    
    public void removeSession(String username) {
        String sql = "DELETE FROM sessions WHERE LOWER(username) = LOWER(?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Erro ao remover sessão do jogador " + username + ":", e);
        }
    }
    
    public void cleanExpiredSessions() {
        String sql = "DELETE FROM sessions WHERE expires_at < CURRENT_TIMESTAMP";
        
        try (Statement stmt = connection.createStatement()) {
            int removed = stmt.executeUpdate(sql);
            if (removed > 0) {
                plugin.getLogger().info("Removidas " + removed + " sessões expiradas.");
            }
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Erro ao limpar sessões expiradas:", e);
        }
    }
    
    public boolean unregisterPlayer(String username) {
        String sql = "DELETE FROM players WHERE LOWER(username) = LOWER(?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Erro ao remover registro do jogador " + username + ":", e);
            return false;
        }
    }
    
    public String getPlayerUUID(String username) {
        String sql = "SELECT uuid FROM players WHERE LOWER(username) = LOWER(?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getString("uuid") : null;
            }
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Erro ao obter UUID do jogador " + username + ":", e);
            return null;
        }
    }
    
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                plugin.getLogger().info("Conexão com banco de dados fechada.");
            } catch (SQLException e) {
                plugin.getLogger().log(Level.WARNING, "Erro ao fechar conexão com banco de dados:", e);
            }
        }
    }
}