package com.authplugin.listeners;

import com.authplugin.AuthPlugin;
import com.authplugin.utils.AuthUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {
    
    private AuthPlugin plugin;
    private AuthUtils authUtils;
    
    public PlayerInteractListener(AuthPlugin plugin) {
        this.plugin = plugin;
        this.authUtils = plugin.getAuthUtils();
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        // Se o jogador não estiver autenticado, cancela a interação
        if (!plugin.isPlayerAuthenticated(player.getUniqueId())) {
            event.setCancelled(true);
            authUtils.sendErrorMessage(player, "Você precisa estar autenticado para interagir com objetos!");
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        
        // Se o jogador não estiver autenticado, cancela a quebra de bloco
        if (!plugin.isPlayerAuthenticated(player.getUniqueId())) {
            event.setCancelled(true);
            authUtils.sendErrorMessage(player, "Você precisa estar autenticado para quebrar blocos!");
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        
        // Se o jogador não estiver autenticado, cancela a colocação de bloco
        if (!plugin.isPlayerAuthenticated(player.getUniqueId())) {
            event.setCancelled(true);
            authUtils.sendErrorMessage(player, "Você precisa estar autenticado para colocar blocos!");
        }
    }
}