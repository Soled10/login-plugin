package com.authplugin.database;

import com.authplugin.AuthPlugin;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.*;
import java.util.UUID;

public class DatabaseManager {
    
    private AuthPlugin plugin;
    private Connection connection;
    
    public DatabaseManager(AuthPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Inicializa o banco de dados SQLite
     */
    public void initialize() {
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
            
            File databaseFile = new File(dataFolder, "auth.db");
            String url = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
            
            connection = DriverManager.getConnection(url);
            createTables();
            
            plugin.getLogger().info("Banco de dados inicializado com sucesso!");
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao inicializar banco de dados: " + e.getMessage());
        }
    }
    
    /**
     * Cria as tabelas necessárias
     */
    private void createTables() throws SQLException {
        String createPlayersTable = "CREATE TABLE IF NOT EXISTS players (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "player_name VARCHAR(16) NOT NULL UNIQUE, " +
                "password VARCHAR(64) NOT NULL, " +
                "salt VARCHAR(32) NOT NULL, " +
                "is_original BOOLEAN DEFAULT FALSE, " +
                "registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        
        String createOriginalNamesTable = "CREATE TABLE IF NOT EXISTS original_names (" +
                "player_name VARCHAR(16) PRIMARY KEY, " +
                "uuid VARCHAR(36) NOT NULL, " +
                "added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createPlayersTable);
            stmt.execute(createOriginalNamesTable);
        }
    }
    
    /**
     * Verifica se um jogador está registrado
     */
    public boolean isPlayerRegistered(UUID uuid) {
        String sql = "SELECT uuid FROM players WHERE uuid = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao verificar se jogador está registrado: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifica se um nome de jogador está registrado
     */
    public boolean isNameRegistered(String playerName) {
        String sql = "SELECT player_name FROM players WHERE player_name = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerName);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao verificar se nome está registrado: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Registra um jogador no banco de dados
     */
    public boolean registerPlayer(UUID uuid, String playerName, String hashedPassword, String salt) {
        String sql = "INSERT INTO players (uuid, player_name, password, salt, is_original) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, playerName);
            stmt.setString(3, hashedPassword);
            stmt.setString(4, salt);
            stmt.setBoolean(5, false); // Contas piratas são false
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao registrar jogador: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtém dados de autenticação de um jogador
     */
    public String[] getPlayerAuthData(UUID uuid) {
        String sql = "SELECT password, salt FROM players WHERE uuid = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new String[]{rs.getString("password"), rs.getString("salt")};
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao obter dados de autenticação: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Atualiza a senha de um jogador
     */
    public boolean updatePlayerPassword(UUID uuid, String newHashedPassword, String newSalt) {
        String sql = "UPDATE players SET password = ?, salt = ? WHERE uuid = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newHashedPassword);
            stmt.setString(2, newSalt);
            stmt.setString(3, uuid.toString());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao atualizar senha: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Adiciona um nome de conta original à lista de proteção
     */
    public boolean addOriginalName(String playerName, UUID uuid) {
        String sql = "INSERT OR REPLACE INTO original_names (player_name, uuid) VALUES (?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerName);
            stmt.setString(2, uuid.toString());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao adicionar nome original: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifica se um nome está na lista de contas originais protegidas
     */
    public boolean isOriginalNameProtected(String playerName) {
        String sql = "SELECT player_name FROM original_names WHERE player_name = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerName);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao verificar nome original protegido: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtém o UUID de uma conta original protegida
     */
    public UUID getOriginalNameUUID(String playerName) {
        String sql = "SELECT uuid FROM original_names WHERE player_name = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerName);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return UUID.fromString(rs.getString("uuid"));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao obter UUID de nome original: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Remove um jogador do banco de dados
     */
    public boolean removePlayer(UUID uuid) {
        String sql = "DELETE FROM players WHERE uuid = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao remover jogador: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Fecha a conexão com o banco de dados
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao fechar conexão com banco de dados: " + e.getMessage());
        }
    }
}