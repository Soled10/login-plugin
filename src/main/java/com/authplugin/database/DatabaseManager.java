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
                "player_name VARCHAR(16) PRIMARY KEY, " +
                "password VARCHAR(64) NOT NULL, " +
                "salt VARCHAR(32) NOT NULL, " +
                "registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createPlayersTable);
        }
    }
    
    /**
     * Verifica se um jogador está registrado pelo nome
     */
    public boolean isPlayerRegistered(String playerName) {
        String sql = "SELECT player_name FROM players WHERE player_name = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerName);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao verificar se jogador está registrado: " + e.getMessage());
            return false;
        }
    }
    
    
    /**
     * Registra um jogador no banco de dados
     */
    public boolean registerPlayer(String playerName, String hashedPassword, String salt) {
        String sql = "INSERT INTO players (player_name, password, salt) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerName);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, salt);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao registrar jogador: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtém dados de autenticação de um jogador pelo nome
     */
    public String[] getPlayerAuthData(String playerName) {
        String sql = "SELECT password, salt FROM players WHERE player_name = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerName);
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
    public boolean updatePlayerPassword(String playerName, String newHashedPassword, String newSalt) {
        String sql = "UPDATE players SET password = ?, salt = ? WHERE player_name = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newHashedPassword);
            stmt.setString(2, newSalt);
            stmt.setString(3, playerName);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao atualizar senha: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Remove um jogador do banco de dados
     */
    public boolean removePlayer(String playerName) {
        String sql = "DELETE FROM players WHERE player_name = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerName);
            
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