package com.authplugin.listeners;

import com.authplugin.AuthPlugin;
import com.authplugin.utils.AuthUtils;
import com.authplugin.utils.OnlineModeSimulator;
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
        
        // Verifica se é conta original de forma assíncrona
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            OnlineModeSimulator.OnlineModeResult result = authUtils.verifyOriginalPlayer(player);
            
            // Executa na thread principal
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (result.isSuccess()) {
                    // Conta original (premium) - autentica automaticamente
                    plugin.setPlayerAuthenticated(player.getUniqueId(), true);
                    plugin.setPlayerLoggedIn(player.getUniqueId(), true);
                    
                    // Adiciona o nome à lista de proteção com UUID offline do jogador
                    UUID officialUUID = result.getOfficialUUID();
                    plugin.getDatabaseManager().addOriginalName(player.getName(), player.getUniqueId());
                    plugin.getLogger().info("✅ Nome protegido para jogador " + player.getName() + " com UUID offline: " + player.getUniqueId());
                    if (officialUUID != null) {
                        plugin.getLogger().info("✅ UUID oficial da API: " + officialUUID);
                    }
                    
                    authUtils.sendSuccessMessage(player, "✅ Conta PREMIUM detectada! Você foi autenticado automaticamente.");
                    authUtils.sendInfoMessage(player, "🔗 UUID oficial associado: " + (officialUUID != null ? officialUUID : "N/A"));
                    removeRestrictions(player);
                } else {
                    // Conta pirata (offline) - verifica se pode usar este nick
                    if (checkOriginalNameProtection(player)) {
                        // Nick pertence a conta original - não permite
                        return;
                    }
                    
                    // Verifica se é conta pirata registrada
                    if (authUtils.isPlayerRegistered(player.getUniqueId())) {
                        authUtils.sendInfoMessage(player, "🔓 Bem-vindo de volta! Use /login <senha> para fazer login.");
                    } else {
                        authUtils.sendInfoMessage(player, "🔓 Bem-vindo! Use /register <senha> <confirmar_senha> para se registrar.");
                    }
                }
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
    
    private boolean checkOriginalNameProtection(Player player) {
        String playerName = player.getName();
        
        // Verifica se o nome está na lista de contas originais protegidas
        if (plugin.getDatabaseManager().isOriginalNameProtected(playerName)) {
            UUID originalUUID = plugin.getDatabaseManager().getOriginalNameUUID(playerName);
            
            // Se o UUID não coincidir com o da conta original, kicka o jogador
            if (originalUUID != null && !originalUUID.equals(player.getUniqueId())) {
                plugin.getLogger().info("❌ Conta pirata tentando usar nome de conta original: " + playerName);
                player.kickPlayer("§c❌ Este nick pertence a uma conta PREMIUM!\n" +
                                "§eVocê não pode usar este nome.\n" +
                                "§a🔓 Use outro nickname para jogar no servidor.");
                return true; // Jogador foi kickado
            }
        }
        
        return false; // Jogador pode continuar
    }
}