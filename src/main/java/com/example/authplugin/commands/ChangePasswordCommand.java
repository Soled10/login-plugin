package com.example.authplugin.commands;

import com.example.authplugin.AuthPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChangePasswordCommand implements CommandExecutor {
    
    private final AuthPlugin plugin;
    
    public ChangePasswordCommand(AuthPlugin plugin) {
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
        
        // Verificar se está autenticado
        if (!plugin.getAuthManager().isAuthenticated(player.getUniqueId())) {
            player.sendMessage(plugin.getConfigManager().getMessage("changepass-not-logged"));
            return true;
        }
        
        // Verificar se está registrado
        if (!plugin.getAuthManager().isRegistered(username)) {
            player.sendMessage(plugin.getConfigManager().getMessage("login-not-registered"));
            return true;
        }
        
        // Verificar argumentos
        if (args.length != 3) {
            player.sendMessage(plugin.getConfigManager().getMessage("changepass-usage"));
            return true;
        }
        
        String oldPassword = args[0];
        String newPassword = args[1];
        String confirmNewPassword = args[2];
        
        // Verificar se as novas senhas coincidem
        if (!newPassword.equals(confirmNewPassword)) {
            player.sendMessage(plugin.getConfigManager().getMessage("changepass-mismatch"));
            return true;
        }
        
        // Verificar tamanho mínimo da nova senha
        if (newPassword.length() < 6) {
            player.sendMessage(plugin.getConfigManager().getMessage("changepass-short"));
            return true;
        }
        
        // Tentar alterar a senha
        if (plugin.getAuthManager().changePassword(username, oldPassword, newPassword)) {
            player.sendMessage(plugin.getConfigManager().getMessage("changepass-success"));
            plugin.getLogger().info("Jogador " + username + " alterou sua senha.");
        } else {
            player.sendMessage(plugin.getConfigManager().getMessage("changepass-wrong-old"));
        }
        
        return true;
    }
}