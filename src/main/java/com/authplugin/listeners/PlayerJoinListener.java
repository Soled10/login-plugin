package com.authplugin.listeners;

import com.authplugin.AuthPlugin;
import com.authplugin.utils.AuthUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerJoinListener implements Listener {
    
    private AuthPlugin plugin;
    private AuthUtils authUtils;
    
    public PlayerJoinListener(AuthPlugin plugin) {
        this.plugin = plugin;
        this.authUtils = plugin.getAuthUtils();
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Define o jogador como não autenticado inicialmente
        plugin.setPlayerAuthenticated(player.getUniqueId(), false);
        plugin.setPlayerLoggedIn(player.getUniqueId(), false);
        
        // Aplica efeitos de restrição até a autenticação
        applyRestrictions(player);
        
        // Verifica se o jogador está registrado
        if (plugin.getDatabaseManager().isPlayerRegistered(player.getName())) {
            authUtils.sendInfoMessage(player, "🔓 Bem-vindo de volta! Use /login <senha> para fazer login.");
        } else {
            authUtils.sendInfoMessage(player, "🔓 Bem-vindo! Use /register <senha> <confirmar_senha> para se registrar.");
        }
    }
    
    private void applyRestrictions(Player player) {
        // Define modo de jogo para adventure (não pode quebrar/bloquear)
        player.setGameMode(GameMode.ADVENTURE);
        
        // Aplica efeito de lentidão
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 255, false, false));
        
        // Aplica efeito de cegueira
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 255, false, false));
        
        // Congela o jogador
        player.setWalkSpeed(0.0f);
        player.setFlySpeed(0.0f);
    }
    
    public void removeRestrictions(Player player) {
        // Remove todos os efeitos de poção
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        
        // Restaura velocidade normal
        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.1f);
        
        // Define modo de jogo para survival
        player.setGameMode(GameMode.SURVIVAL);
    }
    
}