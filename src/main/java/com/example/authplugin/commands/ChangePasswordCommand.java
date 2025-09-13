package com.example.authplugin.commands;

import com.example.authplugin.AuthPlugin;
import com.example.authplugin.managers.AuthManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChangePasswordCommand implements CommandExecutor {
    
    private final AuthPlugin plugin;
    private final AuthManager authManager;
    
    public ChangePasswordCommand(AuthPlugin plugin, AuthManager authManager) {
        this.plugin = plugin;
        this.authManager = authManager;
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
        if (!authManager.isAuthenticated(player)) {
            player.sendMessage("§cVocê precisa estar logado para alterar sua senha!");
            return true;
        }
        
        // Verificar se está registrado
        if (!authManager.isPlayerRegistered(username)) {
            player.sendMessage("§cVocê não está registrado!");
            return true;
        }
        
        // Verificar argumentos
        if (args.length != 2) {
            player.sendMessage("§cUso correto: /changepassword <senha-atual> <nova-senha>");
            return true;
        }
        
        String currentPassword = args[0];
        String newPassword = args[1];
        
        // Validações
        if (newPassword.length() < 4) {
            player.sendMessage("§cA nova senha deve ter pelo menos 4 caracteres!");
            return true;
        }
        
        if (newPassword.length() > 32) {
            player.sendMessage("§cA nova senha não pode ter mais de 32 caracteres!");
            return true;
        }
        
        if (currentPassword.equals(newPassword)) {
            player.sendMessage("§cA nova senha deve ser diferente da senha atual!");
            return true;
        }
        
        // Tentar alterar senha
        if (authManager.changePassword(username, currentPassword, newPassword)) {
            player.sendMessage("§aSenha alterada com sucesso!");
            plugin.getLogger().info("Jogador " + username + " alterou sua senha.");
            
        } else {
            player.sendMessage("§cSenha atual incorreta!");
            plugin.getLogger().warning("Tentativa de alteração de senha falhada para " + username);
        }
        
        return true;
    }
}