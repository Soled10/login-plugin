package com.example.authplugin.managers;

import com.example.authplugin.AuthPlugin;
import com.example.authplugin.utils.MojangAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AuthManager {
    
    private final AuthPlugin plugin;
    private final Set<UUID> authenticatedPlayers = ConcurrentHashMap.newKeySet();
    private final Set<UUID> registeredPlayers = ConcurrentHashMap.newKeySet();
    private final Map<UUID, Long> loginTimeouts = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> loginAttempts = new ConcurrentHashMap<>();
    
    public AuthManager(AuthPlugin plugin) {
        this.plugin = plugin;
        startTimeoutTask();
        startCacheCleanupTask();
    }
    
    /**
     * Verifica se um jogador está autenticado
     */
    public boolean isAuthenticated(UUID uuid) {
        return authenticatedPlayers.contains(uuid);
    }
    
    /**
     * Autentica um jogador
     */
    public void authenticate(Player player) {
        UUID uuid = player.getUniqueId();
        authenticatedPlayers.add(uuid);
        loginTimeouts.remove(uuid);
        loginAttempts.remove(uuid);
        
        // Atualizar último login no banco de dados
        plugin.getDatabaseManager().updateLastLogin(player.getName(), 
            player.getAddress() != null ? player.getAddress().getAddress().getHostAddress() : "unknown");
        
        // Criar sessão se configurado
        int sessionTimeout = plugin.getConfigManager().getSessionTimeout();
        if (sessionTimeout > 0) {
            long expiresAt = System.currentTimeMillis() + (sessionTimeout * 1000L);
            plugin.getDatabaseManager().createSession(
                player.getName(), 
                uuid, 
                player.getAddress() != null ? player.getAddress().getAddress().getHostAddress() : "unknown",
                expiresAt
            );
        }
        
        plugin.getLogger().info("Jogador " + player.getName() + " foi autenticado.");
    }
    
    /**
     * Remove autenticação de um jogador
     */
    public void unauthenticate(UUID uuid) {
        authenticatedPlayers.remove(uuid);
        loginTimeouts.remove(uuid);
        loginAttempts.remove(uuid);
    }
    
    /**
     * Verifica se um jogador está registrado
     */
    public boolean isRegistered(String username) {
        return plugin.getDatabaseManager().isPlayerRegistered(username);
    }
    
    /**
     * Registra um novo jogador
     */
    public boolean registerPlayer(Player player, String password) {
        String username = player.getName();
        UUID uuid = player.getUniqueId();
        String ip = player.getAddress() != null ? player.getAddress().getAddress().getHostAddress() : "unknown";
        
        // Verificar se é uma conta premium
        MojangAPI.PremiumInfo premiumInfo = plugin.getMojangAPI().checkPremium(username);
        
        // Se for premium, bloquear registro de piratas com este nome
        if (premiumInfo.isPremium() && plugin.getConfigManager().isBlockPremiumNames()) {
            // Verificar se o UUID corresponde à conta premium
            if (!uuid.equals(premiumInfo.getUuid())) {
                return false; // Pirata tentando usar nome de conta premium
            }
        }
        
        boolean success = plugin.getDatabaseManager().registerPlayer(
            username, password, uuid, premiumInfo.isPremium(), ip);
        
        if (success) {
            registeredPlayers.add(uuid);
            authenticate(player); // Auto-login após registro
        }
        
        return success;
    }
    
    /**
     * Faz login de um jogador
     */
    public boolean loginPlayer(Player player, String password) {
        String username = player.getName();
        
        if (!plugin.getDatabaseManager().verifyPassword(username, password)) {
            // Incrementar tentativas de login
            int attempts = loginAttempts.getOrDefault(player.getUniqueId(), 0) + 1;
            loginAttempts.put(player.getUniqueId(), attempts);
            plugin.getDatabaseManager().incrementLoginAttempts(username);
            
            return false;
        }
        
        authenticate(player);
        plugin.getDatabaseManager().resetLoginAttempts(username);
        return true;
    }
    
    /**
     * Altera a senha de um jogador
     */
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        if (!plugin.getDatabaseManager().verifyPassword(username, oldPassword)) {
            return false;
        }
        
        return plugin.getDatabaseManager().changePassword(username, newPassword);
    }
    
    /**
     * Verifica se um jogador pode fazer login automaticamente (premium ou sessão válida)
     */
    public boolean canAutoLogin(Player player) {
        String username = player.getName();
        UUID uuid = player.getUniqueId();
        String ip = player.getAddress() != null ? player.getAddress().getAddress().getHostAddress() : "unknown";
        
        // Verificar sessão válida primeiro
        if (plugin.getDatabaseManager().hasValidSession(username, ip)) {
            return true;
        }
        
        // Verificar se é conta premium e está configurado para auto-login
        if (plugin.getConfigManager().isPremiumAutoLogin()) {
            MojangAPI.PremiumInfo premiumInfo = plugin.getMojangAPI().checkPremium(username);
            
            if (premiumInfo.isPremium()) {
                // Verificar se o UUID corresponde (proteção contra piratas)
                if (plugin.getConfigManager().isCheckPremiumUUID()) {
                    return uuid.equals(premiumInfo.getUuid());
                }
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Inicia timeout de login para um jogador
     */
    public void startLoginTimeout(Player player) {
        UUID uuid = player.getUniqueId();
        long timeoutTime = System.currentTimeMillis() + (plugin.getConfigManager().getLoginTimeout() * 1000L);
        loginTimeouts.put(uuid, timeoutTime);
    }
    
    /**
     * Obtém número de tentativas de login de um jogador
     */
    public int getLoginAttempts(Player player) {
        return loginAttempts.getOrDefault(player.getUniqueId(), 0);
    }
    
    /**
     * Verifica se um jogador excedeu o máximo de tentativas de login
     */
    public boolean hasExceededMaxAttempts(Player player) {
        int attempts = getLoginAttempts(player);
        int maxAttempts = plugin.getConfigManager().getMaxLoginAttempts();
        return attempts >= maxAttempts;
    }
    
    /**
     * Teleporta jogador para spawn se configurado
     */
    public void teleportToSpawn(Player player) {
        if (plugin.getConfigManager().isTeleportToSpawn()) {
            World world = Bukkit.getWorld(plugin.getConfigManager().getSpawnWorld());
            if (world != null) {
                Location spawn = new Location(
                    world,
                    plugin.getConfigManager().getSpawnX(),
                    plugin.getConfigManager().getSpawnY(),
                    plugin.getConfigManager().getSpawnZ(),
                    plugin.getConfigManager().getSpawnYaw(),
                    plugin.getConfigManager().getSpawnPitch()
                );
                player.teleport(spawn);
            }
        }
    }
    
    /**
     * Remove registro de um jogador (comando admin)
     */
    public boolean unregisterPlayer(String username) {
        boolean success = plugin.getDatabaseManager().unregisterPlayer(username);
        if (success) {
            // Remover da lista de jogadores registrados se estiver online
            Player player = Bukkit.getPlayerExact(username);
            if (player != null) {
                registeredPlayers.remove(player.getUniqueId());
                unauthenticate(player.getUniqueId());
            }
        }
        return success;
    }
    
    /**
     * Task para verificar timeouts de login
     */
    private void startTimeoutTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                
                loginTimeouts.entrySet().removeIf(entry -> {
                    if (currentTime >= entry.getValue()) {
                        Player player = Bukkit.getPlayer(entry.getKey());
                        if (player != null && player.isOnline()) {
                            player.kickPlayer(plugin.getConfigManager().getMessage("login-timeout"));
                        }
                        return true;
                    }
                    return false;
                });
                
                // Limpar sessões expiradas
                plugin.getDatabaseManager().cleanExpiredSessions();
            }
        }.runTaskTimer(plugin, 20L, 20L); // A cada segundo
    }
    
    /**
     * Task para limpar cache da API Mojang
     */
    private void startCacheCleanupTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getMojangAPI().cleanCache();
            }
        }.runTaskTimer(plugin, 6000L, 6000L); // A cada 5 minutos
    }
    
    /**
     * Limpa dados de um jogador quando ele sai
     */
    public void onPlayerQuit(UUID uuid) {
        authenticatedPlayers.remove(uuid);
        registeredPlayers.remove(uuid);
        loginTimeouts.remove(uuid);
        loginAttempts.remove(uuid);
    }
}