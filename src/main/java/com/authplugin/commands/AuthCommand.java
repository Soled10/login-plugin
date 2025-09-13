package com.authplugin.commands;

import com.authplugin.AuthPlugin;
import com.authplugin.utils.AuthUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AuthCommand implements CommandExecutor {
    
    private AuthPlugin plugin;
    private AuthUtils authUtils;
    
    public AuthCommand(AuthPlugin plugin) {
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
        
        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "register":
                if (args.length < 3) {
                    authUtils.sendErrorMessage(player, "Uso: /auth register <senha> <confirmar_senha>");
                    return true;
                }
                return handleRegister(player, args[1], args[2]);
                
            case "login":
                if (args.length < 2) {
                    authUtils.sendErrorMessage(player, "Uso: /auth login <senha>");
                    return true;
                }
                return handleLogin(player, args[1]);
                
            case "changepassword":
                if (args.length < 3) {
                    authUtils.sendErrorMessage(player, "Uso: /auth changepassword <senha_atual> <nova_senha>");
                    return true;
                }
                return handleChangePassword(player, args[1], args[2]);
                
            case "help":
                sendHelpMessage(player);
                return true;
                
            default:
                authUtils.sendErrorMessage(player, "Subcomando inválido! Use /auth help para ver os comandos disponíveis.");
                return true;
        }
    }
    
    private boolean handleRegister(Player player, String password, String confirmPassword) {
        // Verifica se o jogador já está autenticado
        if (plugin.isPlayerAuthenticated(player.getUniqueId())) {
            authUtils.sendErrorMessage(player, "Você já está autenticado!");
            return true;
        }
        
        // Verifica se é conta original
        if (authUtils.isOriginalPlayer(player)) {
            authUtils.sendErrorMessage(player, "Contas originais não precisam se registrar!");
            return true;
        }
        
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
        
        // Verifica se o nome já está sendo usado por uma conta original
        if (authUtils.isNameUsedByOriginal(player.getName())) {
            authUtils.sendErrorMessage(player, "Este nome pertence a uma conta original! Use outro nome.");
            return true;
        }
        
        // Verifica se o nome já está registrado
        if (authUtils.isNameRegistered(player.getName())) {
            authUtils.sendErrorMessage(player, "Este nome já está registrado! Use /auth login para fazer login.");
            return true;
        }
        
        // Registra o jogador
        if (authUtils.registerPlayer(player.getUniqueId(), player.getName(), password)) {
            authUtils.sendSuccessMessage(player, "Conta registrada com sucesso!");
            authUtils.sendInfoMessage(player, "Você foi autenticado automaticamente.");
            
            // Autentica o jogador automaticamente após o registro
            plugin.setPlayerAuthenticated(player.getUniqueId(), true);
            plugin.setPlayerLoggedIn(player.getUniqueId(), true);
        } else {
            authUtils.sendErrorMessage(player, "Erro ao registrar conta! Tente novamente.");
        }
        
        return true;
    }
    
    private boolean handleLogin(Player player, String password) {
        // Verifica se o jogador já está autenticado
        if (plugin.isPlayerAuthenticated(player.getUniqueId())) {
            authUtils.sendErrorMessage(player, "Você já está autenticado!");
            return true;
        }
        
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
            authUtils.sendErrorMessage(player, "Você não está registrado! Use /auth register para se registrar.");
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
    
    private boolean handleChangePassword(Player player, String currentPassword, String newPassword) {
        // Verifica se o jogador está autenticado
        if (!plugin.isPlayerAuthenticated(player.getUniqueId())) {
            authUtils.sendErrorMessage(player, "Você precisa estar autenticado para alterar a senha!");
            return true;
        }
        
        // Verifica se é conta original
        if (authUtils.isOriginalPlayer(player)) {
            authUtils.sendErrorMessage(player, "Contas originais não podem alterar senha através deste comando!");
            return true;
        }
        
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
        if (authUtils.changePassword(player.getUniqueId(), currentPassword, newPassword)) {
            authUtils.sendSuccessMessage(player, "Senha alterada com sucesso!");
        } else {
            authUtils.sendErrorMessage(player, "Senha atual incorreta ou erro ao alterar senha!");
        }
        
        return true;
    }
    
    private void sendHelpMessage(Player player) {
        authUtils.sendInfoMessage(player, "=== Comandos de Autenticação ===");
        authUtils.sendInfoMessage(player, "/auth register <senha> <confirmar_senha> - Registra uma nova conta");
        authUtils.sendInfoMessage(player, "/auth login <senha> - Faz login na sua conta");
        authUtils.sendInfoMessage(player, "/auth changepassword <senha_atual> <nova_senha> - Altera sua senha");
        authUtils.sendInfoMessage(player, "/auth help - Mostra esta mensagem de ajuda");
        authUtils.sendInfoMessage(player, "=== Comandos Alternativos ===");
        authUtils.sendInfoMessage(player, "/register <senha> <confirmar_senha>");
        authUtils.sendInfoMessage(player, "/login <senha>");
        authUtils.sendInfoMessage(player, "/changepassword <senha_atual> <nova_senha>");
    }
}