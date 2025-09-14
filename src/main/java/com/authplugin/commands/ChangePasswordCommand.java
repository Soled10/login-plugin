package com.authplugin.commands;

import com.authplugin.AuthPlugin;
import com.authplugin.utils.AuthUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChangePasswordCommand implements CommandExecutor {
    
    private AuthPlugin plugin;
    private AuthUtils authUtils;
    
    public ChangePasswordCommand(AuthPlugin plugin) {
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
        
        // Verifica se o jogador está autenticado
        if (!plugin.isPlayerAuthenticated(player.getUniqueId())) {
            authUtils.sendErrorMessage(player, "Você precisa estar autenticado para alterar a senha!");
            return true;
        }
        
        
        // Verifica argumentos
        if (args.length != 2) {
            authUtils.sendErrorMessage(player, "Uso: /changepassword <senha_atual> <nova_senha>");
            return true;
        }
        
        String currentPassword = args[0];
        String newPassword = args[1];
        
        // Validações da nova senha
        if (newPassword.length() < 6) {
            authUtils.sendErrorMessage(player, "A nova senha deve ter pelo menos 6 caracteres!");
            return true;
        }
        
        if (newPassword.length() > 32) {
            authUtils.sendErrorMessage(player, "A nova senha deve ter no máximo 32 caracteres!");
            return true;
        }
        
        if (currentPassword.equals(newPassword)) {
            authUtils.sendErrorMessage(player, "A nova senha deve ser diferente da senha atual!");
            return true;
        }
        
        // Altera a senha
        if (authUtils.changePassword(player.getName(), currentPassword, newPassword)) {
            authUtils.sendSuccessMessage(player, "Senha alterada com sucesso!");
        } else {
            authUtils.sendErrorMessage(player, "Senha atual incorreta ou erro ao alterar senha!");
        }
        
        return true;
    }
}