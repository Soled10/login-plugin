package com.authplugin.listeners;

import com.authplugin.AuthPlugin;
import com.authplugin.utils.AuthUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {
    
    private AuthPlugin plugin;
    private AuthUtils authUtils;
    
    public PlayerChatListener(AuthPlugin plugin) {
        this.plugin = plugin;
        this.authUtils = plugin.getAuthUtils();
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        
        // Se o jogador não estiver autenticado, cancela o chat
        if (!plugin.isPlayerAuthenticated(player.getUniqueId())) {
            event.setCancelled(true);
            authUtils.sendErrorMessage(player, "Você precisa estar autenticado para falar no chat!");
        }
    }
}