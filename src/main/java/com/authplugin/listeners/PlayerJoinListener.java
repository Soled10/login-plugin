package com.authplugin.listeners;

import com.authplugin.AuthPlugin;
import com.authplugin.utils.AuthUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

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
        
        // Verifica se é conta original de forma assíncrona para não travar o servidor
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            boolean isOriginal = authUtils.isOriginalPlayer(player);
            
            // Executa na thread principal
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (isOriginal) {
                    // Autentica automaticamente contas originais
                    plugin.setPlayerAuthenticated(player.getUniqueId(), true);
                    plugin.setPlayerLoggedIn(player.getUniqueId(), true);
                    
                    // Adiciona o nome à lista de proteção
                    plugin.getDatabaseManager().addOriginalName(player.getName(), player.getUniqueId());
                    
                    authUtils.sendSuccessMessage(player, "Conta original detectada! Você foi autenticado automaticamente.");
                    removeRestrictions(player);
                } else {
                    // Verifica se é conta pirata registrada
                    if (authUtils.isPlayerRegistered(player.getUniqueId())) {
                        authUtils.sendInfoMessage(player, "Bem-vindo de volta! Use /login <senha> para fazer login.");
                    } else {
                        authUtils.sendInfoMessage(player, "Bem-vindo! Use /register <senha> <confirmar_senha> para se registrar.");
                    }
                }
                
                // Verifica proteção contra uso de nicks de contas originais
                checkOriginalNameProtection(player);
            });
        });
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
    
    private void removeRestrictions(Player player) {
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
    
    private void checkOriginalNameProtection(Player player) {
        String playerName = player.getName();
        
        // Verifica se o nome está na lista de contas originais protegidas
        if (plugin.getDatabaseManager().isOriginalNameProtected(playerName)) {
            UUID originalUUID = plugin.getDatabaseManager().getOriginalNameUUID(playerName);
            
            // Se o UUID não coincidir com o da conta original, kicka o jogador
            if (originalUUID != null && !originalUUID.equals(player.getUniqueId())) {
                player.kickPlayer("§cEste nome pertence a uma conta original!\n" +
                                "§eVocê não pode usar este nome.\n" +
                                "§aUse outro nome para jogar no servidor.");
                return;
            }
        }
        
        // Verifica se o nome pertence a uma conta original (verificação em tempo real)
        if (authUtils.isNameUsedByOriginal(playerName)) {
            // Se não for a conta original, kicka o jogador
            if (!authUtils.isOriginalPlayer(player)) {
                player.kickPlayer("§cEste nome pertence a uma conta original!\n" +
                                "§eVocê não pode usar este nome.\n" +
                                "§aUse outro nome para jogar no servidor.");
                return;
            }
        }
    }
}