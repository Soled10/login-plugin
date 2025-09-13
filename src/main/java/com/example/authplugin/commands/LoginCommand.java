package com.example.authplugin.commands;

import com.example.authplugin.AuthPlugin;
import com.example.authplugin.managers.AuthManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LoginCommand implements CommandExecutor {
    
    private final AuthPlugin plugin;
    private final AuthManager authManager;
    
    public LoginCommand(AuthPlugin plugin, AuthManager authManager) {
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
        
        // Verificar se já está autenticado
        if (authManager.isAuthenticated(player)) {
            player.sendMessage("§eVocê já está logado!");
            return true;
        }
        
        // Verificar se está registrado
        if (!authManager.isPlayerRegistered(username)) {
            player.sendMessage("§cVocê não está registrado! Use /register <senha> <confirmar-senha>");
            return true;
        }
        
        // Verificar argumentos
        if (args.length != 1) {
            player.sendMessage("§cUso correto: /login <senha>");
            return true;
        }
        
        String password = args[0];
        
        // Validar senha
        if (password.length() < 4) {
            player.sendMessage("§cA senha deve ter pelo menos 4 caracteres!");
            return true;
        }
        
        // Tentar autenticar
        if (authManager.authenticatePlayer(username, password)) {
            authManager.setAuthenticated(player, true);
            plugin.getSessionManager().releasePlayer(player);
            
            player.sendMessage("§aLogin realizado com sucesso!");
            player.sendMessage("§eBem-vindo de volta, " + player.getName() + "!");
            
            plugin.getLogger().info("Jogador " + username + " fez login com sucesso.");
            
        } else {
            player.sendMessage("§cSenha incorreta! Tente novamente.");
            plugin.getLogger().warning("Tentativa de login falhada para " + username);
        }
        
        return true;
    }
}