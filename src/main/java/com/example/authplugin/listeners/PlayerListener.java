package com.example.authplugin.listeners;

import com.example.authplugin.AuthPlugin;
import com.example.authplugin.managers.AuthManager;
import com.example.authplugin.managers.SessionManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener {
    
    private final AuthPlugin plugin;
    private final AuthManager authManager;
    private final SessionManager sessionManager;
    
    public PlayerListener(AuthPlugin plugin, AuthManager authManager, SessionManager sessionManager) {
        this.plugin = plugin;
        this.authManager = authManager;
        this.sessionManager = sessionManager;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String username = player.getName();
        
        // Verificar se deve fazer auto-login (conta premium)
        if (authManager.shouldAutoLogin(player)) {
            authManager.setAuthenticated(player, true);
            
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendMessage("§aLogin automático realizado! Bem-vindo, " + username + "!");
                }
            }.runTaskLater(plugin, 20L); // 1 segundo depois
            
            plugin.getLogger().info("Auto-login para jogador premium: " + username);
            return;
        }
        
        // Preparar jogador para autenticação
        sessionManager.preparePlayer(player);
        
        // Enviar mensagens de boas-vindas
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    player.sendMessage("§e§l=== AUTENTICAÇÃO NECESSÁRIA ===");
                    player.sendMessage("");
                    
                    if (authManager.isPlayerRegistered(username)) {
                        player.sendMessage("§aBem-vindo de volta, " + username + "!");
                        player.sendMessage("§eUse §a/login <senha> §epara entrar no servidor.");
                    } else {
                        player.sendMessage("§eOlá, " + username + "! Você é novo aqui.");
                        player.sendMessage("§eUse §a/register <senha> <confirmar-senha> §epara se registrar.");
                    }
                    
                    player.sendMessage("");
                    player.sendMessage("§cVocê tem 60 segundos para se autenticar!");
                    player.sendMessage("§e§l================================");
                }
            }
        }.runTaskLater(plugin, 5L); // Pequeno delay para garantir que o jogador carregou
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String username = player.getName();
        
        // Limpar dados da sessão
        authManager.logout(player);
        sessionManager.cleanupPlayer(username);
        
        plugin.getLogger().info("Jogador " + username + " saiu do servidor.");
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        if (!authManager.isAuthenticated(player)) {
            // Permitir apenas rotação da cabeça
            if (event.getFrom().getX() != event.getTo().getX() || 
                event.getFrom().getZ() != event.getTo().getZ() ||
                event.getFrom().getY() != event.getTo().getY()) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        
        if (!authManager.isAuthenticated(player)) {
            event.setCancelled(true);
            player.sendMessage("§cVocê precisa fazer login para falar no chat!");
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().toLowerCase();
        
        if (!authManager.isAuthenticated(player)) {
            // Permitir apenas comandos de autenticação
            if (!command.startsWith("/login") && 
                !command.startsWith("/l ") &&
                !command.startsWith("/register") && 
                !command.startsWith("/reg ")) {
                
                event.setCancelled(true);
                player.sendMessage("§cVocê só pode usar comandos de login e registro!");
                
                if (authManager.isPlayerRegistered(player.getName())) {
                    player.sendMessage("§eUse §a/login <senha> §epara entrar.");
                } else {
                    player.sendMessage("§eUse §a/register <senha> <confirmar-senha> §epara se registrar.");
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        
        if (!authManager.isAuthenticated(player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        
        if (!authManager.isAuthenticated(player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            
            if (!authManager.isAuthenticated(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            
            if (!authManager.isAuthenticated(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            
            if (!authManager.isAuthenticated(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if (!authManager.isAuthenticated(player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        
        if (!authManager.isAuthenticated(player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        
        if (!authManager.isAuthenticated(player)) {
            event.setCancelled(true);
        }
    }
}