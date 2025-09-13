package com.example.authplugin.managers;

import com.example.authplugin.AuthPlugin;
import com.example.authplugin.database.DatabaseManager;
import com.example.authplugin.models.PlayerData;
import com.example.authplugin.utils.MojangAPI;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AuthManager {
    
    private final AuthPlugin plugin;
    private final DatabaseManager databaseManager;
    private final Set<String> authenticatedPlayers;
    private final MojangAPI mojangAPI;
    
    public AuthManager(AuthPlugin plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.authenticatedPlayers = new HashSet<>();
        this.mojangAPI = new MojangAPI();
    }
    
    public boolean isAuthenticated(Player player) {
        return authenticatedPlayers.contains(player.getName().toLowerCase());
    }
    
    public void setAuthenticated(Player player, boolean authenticated) {
        String playerName = player.getName().toLowerCase();
        if (authenticated) {
            authenticatedPlayers.add(playerName);
            databaseManager.updateLastLogin(player.getName(), player.getAddress().getAddress().getHostAddress());
        } else {
            authenticatedPlayers.remove(playerName);
        }
    }
    
    public boolean isPlayerRegistered(String username) {
        return databaseManager.isPlayerRegistered(username);
    }
    
    public boolean registerPlayer(Player player, String password) {
        String username = player.getName();
        UUID playerUuid = player.getUniqueId();
        String ipAddress = player.getAddress().getAddress().getHostAddress();
        
        // Verificar se é conta premium
        boolean isPremium = mojangAPI.isPremiumAccount(username, playerUuid);
        
        // Proteção contra pirates usando nicks de contas originais
        if (!isPremium) {
            UUID originalUuid = mojangAPI.getUUIDFromUsername(username);
            if (originalUuid != null && !originalUuid.equals(playerUuid)) {
                return false; // Nick pertence a uma conta premium, mas UUID não confere
            }
        }
        
        return databaseManager.registerPlayer(username, password, playerUuid, ipAddress, isPremium);
    }
    
    public boolean authenticatePlayer(String username, String password) {
        return databaseManager.authenticatePlayer(username, password);
    }
    
    public boolean changePassword(String username, String currentPassword, String newPassword) {
        if (!authenticatePlayer(username, currentPassword)) {
            return false;
        }
        
        return databaseManager.updatePassword(username, newPassword);
    }
    
    public PlayerData getPlayerData(String username) {
        return databaseManager.getPlayerData(username);
    }
    
    public boolean shouldAutoLogin(Player player) {
        String username = player.getName();
        UUID playerUuid = player.getUniqueId();
        
        // Verificar se é conta premium através da Mojang API
        boolean isPremium = mojangAPI.isPremiumAccount(username, playerUuid);
        
        if (isPremium) {
            // Se é premium e está registrado, fazer auto-login
            if (isPlayerRegistered(username)) {
                PlayerData playerData = getPlayerData(username);
                if (playerData != null && playerData.isPremium()) {
                    return true;
                }
            } else {
                // Se é premium mas não está registrado, registrar automaticamente
                String autoPassword = UUID.randomUUID().toString().substring(0, 8);
                registerPlayer(player, autoPassword);
                return true;
            }
        }
        
        return false;
    }
    
    public void logout(Player player) {
        setAuthenticated(player, false);
    }
    
    public void logoutAll() {
        authenticatedPlayers.clear();
    }
}