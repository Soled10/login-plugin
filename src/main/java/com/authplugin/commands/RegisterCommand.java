package com.authplugin.commands;

import com.authplugin.AuthPlugin;
import com.authplugin.utils.AuthUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegisterCommand implements CommandExecutor {
    
    private AuthPlugin plugin;
    private AuthUtils authUtils;
    
    public RegisterCommand(AuthPlugin plugin) {
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
        if (args.length != 2) {
            authUtils.sendErrorMessage(player, "Uso: /register <senha> <confirmar_senha>");
            return true;
        }
        
        String password = args[0];
        String confirmPassword = args[1];
        
        // Validações da senha
        if (password.length() < 6) {
            authUtils.sendErrorMessage(player, "A senha deve ter pelo menos 6 caracteres!");
            return true;
        }
        
        if (password.length() > 32) {
            authUtils.sendErrorMessage(player, "A senha deve ter no máximo 32 caracteres!");
            return true;
        }
        
        if (!password.equals(confirmPassword)) {
            authUtils.sendErrorMessage(player, "As senhas não coincidem!");
            return true;
        }
        
        // Verifica se o nome já está registrado
        if (plugin.getDatabaseManager().isPlayerRegistered(player.getName())) {
            authUtils.sendErrorMessage(player, "Este nome já está registrado! Use /login para fazer login.");
            return true;
        }
        
        // Registra o jogador
        if (authUtils.registerPlayer(player.getName(), password)) {
            authUtils.sendSuccessMessage(player, "Conta registrada com sucesso!");
            authUtils.sendInfoMessage(player, "Você foi autenticado automaticamente.");
            
            // Autentica o jogador automaticamente após o registro
            plugin.setPlayerAuthenticated(player.getUniqueId(), true);
            plugin.setPlayerLoggedIn(player.getUniqueId(), true);
            
            // Remove restrições após registro bem-sucedido
            removePlayerRestrictions(player);
        } else {
            authUtils.sendErrorMessage(player, "Erro ao registrar conta! Tente novamente.");
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