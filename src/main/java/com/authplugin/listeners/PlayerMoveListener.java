package com.authplugin.listeners;

import com.authplugin.AuthPlugin;
import com.authplugin.utils.AuthUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {
    
    private AuthPlugin plugin;
    private AuthUtils authUtils;
    
    public PlayerMoveListener(AuthPlugin plugin) {
        this.plugin = plugin;
        this.authUtils = plugin.getAuthUtils();
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        // Se o jogador não estiver autenticado, cancela o movimento
        if (!plugin.isPlayerAuthenticated(player.getUniqueId())) {
            Location from = event.getFrom();
            Location to = event.getTo();
            
            // Permite apenas movimento vertical (pulo/queda) mas cancela movimento horizontal
            if (from.getX() != to.getX() || from.getZ() != to.getZ()) {
                event.setCancelled(true);
            }
        }
    }
}