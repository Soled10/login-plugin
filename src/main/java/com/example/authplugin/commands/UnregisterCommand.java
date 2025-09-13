package com.example.authplugin.commands;

import com.example.authplugin.AuthPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnregisterCommand implements CommandExecutor {
    
    private final AuthPlugin plugin;
    
    public UnregisterCommand(AuthPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Verificar permissão
        if (!sender.hasPermission("authplugin.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("admin-no-permission"));
            return true;
        }
        
        // Verificar argumentos
        if (args.length != 1) {
            sender.sendMessage("§cUso: /unregister <jogador>");
            return true;
        }
        
        String targetPlayer = args[0];
        
        // Verificar se o jogador está registrado
        if (!plugin.getAuthManager().isRegistered(targetPlayer)) {
            String message = plugin.getConfigManager().getMessage("admin-unregister-not-found")
                .replace("%player%", targetPlayer);
            sender.sendMessage(message);
            return true;
        }
        
        // Tentar remover o registro
        if (plugin.getAuthManager().unregisterPlayer(targetPlayer)) {
            String message = plugin.getConfigManager().getMessage("admin-unregister-success")
                .replace("%player%", targetPlayer);
            sender.sendMessage(message);
            
            // Se o jogador estiver online, kická-lo
            Player onlinePlayer = Bukkit.getPlayerExact(targetPlayer);
            if (onlinePlayer != null && onlinePlayer.isOnline()) {
                onlinePlayer.kickPlayer("§cSeu registro foi removido por um administrador.");
            }
            
            plugin.getLogger().info("Administrador " + sender.getName() + " removeu o registro do jogador " + targetPlayer);
            
        } else {
            sender.sendMessage("§cErro ao remover registro do jogador " + targetPlayer);
        }
        
        return true;
    }
}