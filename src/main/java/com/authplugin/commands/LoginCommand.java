package com.authplugin.commands;

import com.authplugin.AuthPlugin;
import com.authplugin.utils.AuthUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LoginCommand implements CommandExecutor {
    
    private AuthPlugin plugin;
    private AuthUtils authUtils;
    
    public LoginCommand(AuthPlugin plugin) {
        this.plugin = plugin;
        this.authUtils = plugin.getAuthUtils();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cEste comando só pode ser executado por jogadores!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Verifica se o jogador já está autenticado
        if (plugin.isPlayerAuthenticated(player.getUniqueId())) {
            authUtils.sendErrorMessage(player, "Você já está autenticado!");
            return true;
        }
        
        // Verifica argumentos
        if (args.length != 1) {
            authUtils.sendErrorMessage(player, "Uso: /login <senha>");
            return true;
        }
        
        String password = args[0];
        
        // Verifica se o jogador está registrado
        if (!plugin.getDatabaseManager().isPlayerRegistered(player.getName())) {
            authUtils.sendErrorMessage(player, "Você não está registrado! Use /register para se registrar.");
            return true;
        }
        
        // Autentica o jogador
        if (authUtils.authenticatePlayer(player, password)) {
            authUtils.sendSuccessMessage(player, "Login realizado com sucesso!");
            // Remove restrições após login bem-sucedido
            removePlayerRestrictions(player);
        } else {
            authUtils.sendErrorMessage(player, "Senha incorreta!");
        }
        
        return true;
    }
    
    private void removePlayerRestrictions(Player player) {
        // Remove todos os efeitos de poção
        for (org.bukkit.potion.PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        
        // Restaura velocidade normal
        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.1f);
        
        // Define modo de jogo para survival
        player.setGameMode(org.bukkit.GameMode.SURVIVAL);
    }
}