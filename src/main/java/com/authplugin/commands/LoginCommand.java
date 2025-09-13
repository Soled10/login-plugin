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
        
        // Se for conta original, autentica automaticamente
        if (authUtils.isOriginalPlayer(player)) {
            authUtils.sendSuccessMessage(player, "Conta original detectada! Você foi autenticado automaticamente.");
            plugin.setPlayerAuthenticated(player.getUniqueId(), true);
            plugin.setPlayerLoggedIn(player.getUniqueId(), true);
            
            // Adiciona o nome à lista de proteção
            plugin.getDatabaseManager().addOriginalName(player.getName(), player.getUniqueId());
            return true;
        }
        
        // Se for conta pirata, verifica no banco de dados
        if (!authUtils.isPlayerRegistered(player.getUniqueId())) {
            authUtils.sendErrorMessage(player, "Você não está registrado! Use /register para se registrar.");
            return true;
        }
        
        // Autentica o jogador
        if (authUtils.authenticatePlayer(player, password)) {
            authUtils.sendSuccessMessage(player, "Login realizado com sucesso!");
        } else {
            authUtils.sendErrorMessage(player, "Senha incorreta!");
        }
        
        return true;
    }
}