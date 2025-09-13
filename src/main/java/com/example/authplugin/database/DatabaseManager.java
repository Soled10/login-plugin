package com.example.authplugin.database;

import com.example.authplugin.AuthPlugin;
import com.example.authplugin.models.PlayerData;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseManager {
    
    private final AuthPlugin plugin;
    private Connection connection;
    
    public DatabaseManager(AuthPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void initialize() {
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
            
            String url = "jdbc:sqlite:" + dataFolder.getAbsolutePath() + "/players.db";
            connection = DriverManager.getConnection(url);
            
            createTables();
            plugin.getLogger().info("Database conectado com sucesso!");
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao conectar com database!", e);
        }
    }
    
    private void createTables() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS players (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username VARCHAR(16) NOT NULL UNIQUE," +
                    "uuid VARCHAR(36)," +
                    "password_hash VARCHAR(255) NOT NULL," +
                    "is_premium BOOLEAN DEFAULT FALSE," +
                    "ip_address VARCHAR(45)," +
                    "last_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
    
    public boolean isPlayerRegistered(String username) {
        String sql = "SELECT COUNT(*) FROM players WHERE LOWER(username) = LOWER(?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao verificar se jogador está registrado!", e);
        }
        
        return false;
    }
    
    public boolean registerPlayer(String username, String password, UUID uuid, String ipAddress, boolean isPremium) {
        String sql = "INSERT INTO players (username, uuid, password_hash, is_premium, ip_address) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, uuid != null ? uuid.toString() : null);
            pstmt.setString(3, BCrypt.hashpw(password, BCrypt.gensalt()));
            pstmt.setBoolean(4, isPremium);
            pstmt.setString(5, ipAddress);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao registrar jogador!", e);
        }
        
        return false;
    }
    
    public boolean authenticatePlayer(String username, String password) {
        String sql = "SELECT password_hash FROM players WHERE LOWER(username) = LOWER(?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                return BCrypt.checkpw(password, storedHash);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao autenticar jogador!", e);
        }
        
        return false;
    }
    
    public PlayerData getPlayerData(String username) {
        String sql = "SELECT * FROM players WHERE LOWER(username) = LOWER(?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                PlayerData data = new PlayerData();
                data.setUsername(rs.getString("username"));
                
                String uuidString = rs.getString("uuid");
                if (uuidString != null) {
                    data.setUuid(UUID.fromString(uuidString));
                }
                
                data.setPremium(rs.getBoolean("is_premium"));
                data.setIpAddress(rs.getString("ip_address"));
                data.setLastLogin(rs.getTimestamp("last_login"));
                data.setRegistrationDate(rs.getTimestamp("registration_date"));
                
                return data;
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao buscar dados do jogador!", e);
        }
        
        return null;
    }
    
    public boolean updatePassword(String username, String newPassword) {
        String sql = "UPDATE players SET password_hash = ? WHERE LOWER(username) = LOWER(?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
            pstmt.setString(2, username);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao atualizar senha!", e);
        }
        
        return false;
    }
    
    public void updateLastLogin(String username, String ipAddress) {
        String sql = "UPDATE players SET last_login = CURRENT_TIMESTAMP, ip_address = ? WHERE LOWER(username) = LOWER(?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, ipAddress);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao atualizar último login!", e);
        }
    }
    
    public boolean isPremiumPlayer(String username) {
        String sql = "SELECT is_premium FROM players WHERE LOWER(username) = LOWER(?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBoolean("is_premium");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao verificar se jogador é premium!", e);
        }
        
        return false;
    }
    
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao fechar conexão com database!", e);
        }
    }
}