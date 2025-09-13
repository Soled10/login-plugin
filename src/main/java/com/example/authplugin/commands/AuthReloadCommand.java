package com.example.authplugin.commands;

import com.example.authplugin.AuthPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AuthReloadCommand implements CommandExecutor {
    
    private final AuthPlugin plugin;
    
    public AuthReloadCommand(AuthPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Verificar permissão
        if (!sender.hasPermission("authplugin.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("admin-no-permission"));
            return true;
        }
        
        try {
            // Recarregar configuração
            plugin.getConfigManager().reloadConfig();
            
            // Limpar cache da API Mojang
            plugin.getMojangAPI().clearCache();
            
            sender.sendMessage(plugin.getConfigManager().getMessage("admin-reload"));
            plugin.getLogger().info("Configuração recarregada por " + sender.getName());
            
        } catch (Exception e) {
            sender.sendMessage("§cErro ao recarregar configuração: " + e.getMessage());
            plugin.getLogger().warning("Erro ao recarregar configuração: " + e.getMessage());
        }
        
        return true;
    }
}