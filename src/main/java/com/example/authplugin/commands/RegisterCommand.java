package com.example.authplugin.commands;

import com.example.authplugin.AuthPlugin;
import com.example.authplugin.managers.AuthManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegisterCommand implements CommandExecutor {
    
    private final AuthPlugin plugin;
    private final AuthManager authManager;
    
    public RegisterCommand(AuthPlugin plugin, AuthManager authManager) {
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
        
        // Verificar se já está registrado
        if (authManager.isPlayerRegistered(username)) {
            player.sendMessage("§cVocê já está registrado! Use /login <senha>");
            return true;
        }
        
        // Verificar argumentos
        if (args.length != 2) {
            player.sendMessage("§cUso correto: /register <senha> <confirmar-senha>");
            return true;
        }
        
        String password = args[0];
        String confirmPassword = args[1];
        
        // Validações
        if (password.length() < 4) {
            player.sendMessage("§cA senha deve ter pelo menos 4 caracteres!");
            return true;
        }
        
        if (password.length() > 32) {
            player.sendMessage("§cA senha não pode ter mais de 32 caracteres!");
            return true;
        }
        
        if (!password.equals(confirmPassword)) {
            player.sendMessage("§cAs senhas não coincidem!");
            return true;
        }
        
        // Tentar registrar
        if (authManager.registerPlayer(player, password)) {
            authManager.setAuthenticated(player, true);
            plugin.getSessionManager().releasePlayer(player);
            
            player.sendMessage("§aRegistro realizado com sucesso!");
            player.sendMessage("§eVocê foi automaticamente logado.");
            player.sendMessage("§eBem-vindo ao servidor, " + player.getName() + "!");
            
            plugin.getLogger().info("Jogador " + username + " se registrou com sucesso.");
            
        } else {
            player.sendMessage("§cErro ao registrar! Este nome de usuário pode já pertencer a uma conta premium.");
            player.sendMessage("§cSe você é o dono desta conta, entre com a versão original do Minecraft.");
            
            plugin.getLogger().warning("Falha no registro para " + username + " - possível tentativa de usar nick premium");
        }
        
        return true;
    }
}