package com.authplugin.listeners;

import com.authplugin.AuthPlugin;
import com.authplugin.utils.AuthUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDamageListener implements Listener {
    
    private AuthPlugin plugin;
    private AuthUtils authUtils;
    
    public PlayerDamageListener(AuthPlugin plugin) {
        this.plugin = plugin;
        this.authUtils = plugin.getAuthUtils();
    }
    
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        // Se não for um jogador, ignora
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        
        // Se o jogador não estiver autenticado, cancela o dano
        if (!plugin.isPlayerAuthenticated(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        
        // Se o jogador não estiver autenticado, cancela a morte
        if (!plugin.isPlayerAuthenticated(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }
}