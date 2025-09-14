package com.authplugin.utils;

import com.authplugin.AuthPlugin;
import com.authplugin.database.DatabaseManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;

public class AuthUtils {
    
    private AuthPlugin plugin;
    private DatabaseManager databaseManager;
    
    public AuthUtils(AuthPlugin plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
    }
    
    /**
     * Criptografa uma senha usando SHA-256 com salt
     */
    public String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String saltedPassword = password + salt;
            byte[] hashedBytes = md.digest(saltedPassword.getBytes());
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            plugin.getLogger().severe("Erro ao criptografar senha: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Gera um salt aleatório
     */
    public String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        
        StringBuilder sb = new StringBuilder();
        for (byte b : salt) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    /**
     * Verifica se uma senha está correta
     */
    public boolean verifyPassword(String password, String hashedPassword, String salt) {
        String hashedInput = hashPassword(password, salt);
        return hashedInput != null && hashedInput.equals(hashedPassword);
    }
    
    
    /**
     * Verifica se um jogador está registrado no banco de dados pelo nome
     */
    public boolean isPlayerRegistered(String playerName) {
        return databaseManager.isPlayerRegistered(playerName);
    }
    
    /**
     * Registra um jogador no banco de dados
     */
    public boolean registerPlayer(String playerName, String password) {
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);
        
        if (hashedPassword == null) {
            return false;
        }
        
        return databaseManager.registerPlayer(playerName, hashedPassword, salt);
    }
    
    /**
     * Autentica um jogador
     */
    public boolean authenticatePlayer(Player player, String password) {
        UUID uuid = player.getUniqueId();
        
        // Verifica se o jogador está registrado
        if (!isPlayerRegistered(player.getName())) {
            return false;
        }
        
        // Obtém dados de autenticação do banco
        String[] authData = databaseManager.getPlayerAuthData(player.getName());
        if (authData != null) {
            String hashedPassword = authData[0];
            String salt = authData[1];
            
            if (verifyPassword(password, hashedPassword, salt)) {
                plugin.setPlayerAuthenticated(uuid, true);
                plugin.setPlayerLoggedIn(uuid, true);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Altera a senha de um jogador
     */
    public boolean changePassword(String playerName, String currentPassword, String newPassword) {
        if (!isPlayerRegistered(playerName)) {
            return false;
        }
        
        String[] authData = databaseManager.getPlayerAuthData(playerName);
        if (authData == null) {
            return false;
        }
        
        String hashedPassword = authData[0];
        String salt = authData[1];
        
        // Verifica se a senha atual está correta
        if (!verifyPassword(currentPassword, hashedPassword, salt)) {
            return false;
        }
        
        // Gera nova senha
        String newSalt = generateSalt();
        String newHashedPassword = hashPassword(newPassword, newSalt);
        
        if (newHashedPassword == null) {
            return false;
        }
        
        return databaseManager.updatePlayerPassword(playerName, newHashedPassword, newSalt);
    }
    
    
    /**
     * Formata mensagens com cores
     */
    public String formatMessage(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    /**
     * Envia mensagem formatada para o jogador
     */
    public void sendMessage(Player player, String message) {
        player.sendMessage(formatMessage(message));
    }
    
    /**
     * Envia mensagem de erro para o jogador
     */
    public void sendErrorMessage(Player player, String message) {
        sendMessage(player, "&c" + message);
    }
    
    /**
     * Envia mensagem de sucesso para o jogador
     */
    public void sendSuccessMessage(Player player, String message) {
        sendMessage(player, "&a" + message);
    }
    
    /**
     * Envia mensagem de informação para o jogador
     */
    public void sendInfoMessage(Player player, String message) {
        sendMessage(player, "&e" + message);
    }
}