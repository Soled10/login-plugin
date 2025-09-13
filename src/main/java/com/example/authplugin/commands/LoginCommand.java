package com.example.authplugin.commands;

import com.example.authplugin.AuthPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LoginCommand implements CommandExecutor {
    
    private final AuthPlugin plugin;
    
    public LoginCommand(AuthPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cEste comando só pode ser usado por jogadores!");
            return true;
        }
        
        Player player = (Player) sender;
        String username = player.getName();
        
        // Verificar se já está autenticado
        if (plugin.getAuthManager().isAuthenticated(player.getUniqueId())) {
            player.sendMessage(plugin.getConfigManager().getMessage("login-already"));
            return true;
        }
        
        // Verificar se está registrado
        if (!plugin.getAuthManager().isRegistered(username)) {
            player.sendMessage(plugin.getConfigManager().getMessage("login-not-registered"));
            return true;
        }
        
        // Verificar argumentos
        if (args.length != 1) {
            player.sendMessage(plugin.getConfigManager().getMessage("login-usage"));
            return true;
        }
        
        // Verificar se excedeu máximo de tentativas
        if (plugin.getAuthManager().hasExceededMaxAttempts(player)) {
            player.kickPlayer(plugin.getConfigManager().getMessage("login-max-attempts"));
            return true;
        }
        
        String password = args[0];
        
        // Tentar fazer login
        if (plugin.getAuthManager().loginPlayer(player, password)) {
            player.sendMessage(plugin.getConfigManager().getMessage("login-success"));
            plugin.getLogger().info("Jogador " + username + " fez login com sucesso.");
        } else {
            int attempts = plugin.getAuthManager().getLoginAttempts(player);
            int maxAttempts = plugin.getConfigManager().getMaxLoginAttempts();
            int remaining = maxAttempts - attempts;
            
            if (remaining <= 0) {
                player.kickPlayer(plugin.getConfigManager().getMessage("login-max-attempts"));
            } else {
                String message = plugin.getConfigManager().getMessage("login-failed")
                    .replace("%attempts%", String.valueOf(remaining));
                player.sendMessage(message);
            }
        }
        
        return true;
    }
}