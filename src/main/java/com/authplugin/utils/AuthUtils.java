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
    private MojangAPI mojangAPI;
    private UserOnlineModeChecker userOnlineModeChecker;
    
    public AuthUtils(AuthPlugin plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
        this.mojangAPI = plugin.getMojangAPI();
        this.userOnlineModeChecker = new UserOnlineModeChecker(plugin);
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
     * Verifica se um jogador é de conta original
     * Método: Simula online-mode=true para o usuário específico
     */
    public boolean isOriginalPlayer(Player player) {
        plugin.getLogger().info("🔍 Verificando conta original para: " + player.getName() + " (" + player.getUniqueId() + ")");
        
        // Verifica se o servidor está em online-mode
        boolean isOnlineMode = plugin.getServer().getOnlineMode();
        
        if (isOnlineMode) {
            // Se estiver em online-mode, todos os jogadores são originais
            plugin.getLogger().info("✅ Servidor em online-mode - conta considerada PREMIUM: " + player.getName());
            return true;
        }
        
        // Se estiver em offline-mode, simula online-mode=true para este usuário
        try {
            boolean passedOnlineModeCheck = userOnlineModeChecker.verifyUserOnlineMode(player.getName(), player.getUniqueId()).get();
            
            if (passedOnlineModeCheck) {
                plugin.getLogger().info("✅ Usuário passou na verificação online-mode - PREMIUM: " + player.getName());
                return true;
            } else {
                plugin.getLogger().info("❌ Usuário falhou na verificação online-mode - PIRATA: " + player.getName());
                return false;
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Erro na verificação online-mode: " + e.getMessage());
            // Em caso de erro, considera como pirata
            plugin.getLogger().info("❌ Erro na verificação - considerando como PIRATA: " + player.getName());
            return false;
        }
    }
    
    /**
     * Verifica se um UUID é de modo online (premium)
     */
    private boolean isOnlineModeUUID(UUID uuid) {
        // UUIDs de modo online (premium) têm versão 4
        // UUIDs de modo offline (pirata) têm versão 3
        return uuid.version() == 4; // UUID v4 indica modo online (premium)
    }
    
    /**
     * Verifica se um UUID é de modo offline (pirata)
     */
    private boolean isOfflineModeUUID(UUID uuid) {
        // UUIDs de modo offline têm um padrão específico
        // Eles são gerados baseados no nome do jogador
        return uuid.version() == 3; // UUID v3 indica modo offline
    }
    
    /**
     * Verifica se um jogador está registrado no banco de dados
     */
    public boolean isPlayerRegistered(UUID uuid) {
        return databaseManager.isPlayerRegistered(uuid);
    }
    
    /**
     * Registra um jogador no banco de dados
     */
    public boolean registerPlayer(UUID uuid, String playerName, String password) {
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);
        
        if (hashedPassword == null) {
            return false;
        }
        
        return databaseManager.registerPlayer(uuid, playerName, hashedPassword, salt);
    }
    
    /**
     * Autentica um jogador
     */
    public boolean authenticatePlayer(Player player, String password) {
        UUID uuid = player.getUniqueId();
        
        // Se for conta original, autentica automaticamente
        if (isOriginalPlayer(player)) {
            plugin.setPlayerAuthenticated(uuid, true);
            plugin.setPlayerLoggedIn(uuid, true);
            return true;
        }
        
        // Se for conta pirata, verifica no banco de dados
        if (isPlayerRegistered(uuid)) {
            String[] authData = databaseManager.getPlayerAuthData(uuid);
            if (authData != null) {
                String hashedPassword = authData[0];
                String salt = authData[1];
                
                if (verifyPassword(password, hashedPassword, salt)) {
                    plugin.setPlayerAuthenticated(uuid, true);
                    plugin.setPlayerLoggedIn(uuid, true);
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Altera a senha de um jogador
     */
    public boolean changePassword(UUID uuid, String currentPassword, String newPassword) {
        if (!isPlayerRegistered(uuid)) {
            return false;
        }
        
        String[] authData = databaseManager.getPlayerAuthData(uuid);
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
        
        return databaseManager.updatePlayerPassword(uuid, newHashedPassword, newSalt);
    }
    
    /**
     * Verifica se um nome de jogador já está sendo usado por uma conta original
     */
    public boolean isNameUsedByOriginal(String playerName) {
        return mojangAPI.isOriginalAccount(playerName);
    }
    
    /**
     * Verifica se um nome de jogador já está registrado no banco de dados
     */
    public boolean isNameRegistered(String playerName) {
        return databaseManager.isNameRegistered(playerName);
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