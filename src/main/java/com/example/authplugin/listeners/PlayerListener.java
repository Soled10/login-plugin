package com.example.authplugin.listeners;

import com.example.authplugin.AuthPlugin;
import com.example.authplugin.utils.MojangAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener {
    
    private final AuthPlugin plugin;
    
    public PlayerListener(AuthPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String username = player.getName();
        
        // Teleportar para spawn
        plugin.getAuthManager().teleportToSpawn(player);
        
        // Verificar se pode fazer auto-login
        if (plugin.getAuthManager().canAutoLogin(player)) {
            // Auto-login para contas premium ou com sessão válida
            plugin.getAuthManager().authenticate(player);
            
            // Verificar se é conta premium para mensagem especial
            new BukkitRunnable() {
                @Override
                public void run() {
                    MojangAPI.PremiumInfo premiumInfo = plugin.getMojangAPI().checkPremium(username);
                    if (premiumInfo.isPremium()) {
                        player.sendMessage(plugin.getConfigManager().getMessage("premium-welcome")
                            .replace("%player%", player.getName()));
                    }
                }
            }.runTaskAsynchronously(plugin);
            
        } else {
            // Iniciar timeout de login
            plugin.getAuthManager().startLoginTimeout(player);
            
            // Enviar mensagens apropriadas
            if (plugin.getAuthManager().isRegistered(username)) {
                player.sendMessage(plugin.getConfigManager().getMessage("need-login"));
            } else {
                player.sendMessage(plugin.getConfigManager().getMessage("need-register"));
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getAuthManager().onPlayerQuit(event.getPlayer().getUniqueId());
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getAuthManager().isAuthenticated(player.getUniqueId())) {
            // Permitir apenas rotação, não movimento
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
        
        if (!plugin.getAuthManager().isAuthenticated(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfigManager().getMessage("not-logged"));
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().toLowerCase();
        
        if (!plugin.getAuthManager().isAuthenticated(player.getUniqueId())) {
            // Permitir apenas comandos de autenticação
            String[] allowedCommands = {"/register", "/reg", "/login", "/l"};
            boolean isAllowed = false;
            
            for (String allowedCmd : allowedCommands) {
                if (command.startsWith(allowedCmd + " ") || command.equals(allowedCmd)) {
                    isAllowed = true;
                    break;
                }
            }
            
            if (!isAllowed) {
                event.setCancelled(true);
                player.sendMessage(plugin.getConfigManager().getMessage("not-logged"));
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getAuthManager().isAuthenticated(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfigManager().getMessage("not-logged"));
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getAuthManager().isAuthenticated(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfigManager().getMessage("not-logged"));
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            
            if (!plugin.getAuthManager().isAuthenticated(player.getUniqueId())) {
                event.setCancelled(true);
                player.sendMessage(plugin.getConfigManager().getMessage("not-logged"));
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            
            if (!plugin.getAuthManager().isAuthenticated(player.getUniqueId())) {
                event.setCancelled(true);
                player.sendMessage(plugin.getConfigManager().getMessage("not-logged"));
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            
            if (!plugin.getAuthManager().isAuthenticated(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            
            if (!plugin.getAuthManager().isAuthenticated(player.getUniqueId())) {
                event.setCancelled(true);
                player.sendMessage(plugin.getConfigManager().getMessage("not-logged"));
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getAuthManager().isAuthenticated(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfigManager().getMessage("not-logged"));
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getAuthManager().isAuthenticated(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getAuthManager().isAuthenticated(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfigManager().getMessage("not-logged"));
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getAuthManager().isAuthenticated(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfigManager().getMessage("not-logged"));
        }
    }
}