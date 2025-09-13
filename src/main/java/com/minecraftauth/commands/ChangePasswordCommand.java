package com.minecraftauth.commands;

import com.minecraftauth.MinecraftAuth;
import com.minecraftauth.auth.AuthManager;
import com.minecraftauth.auth.AuthResult;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChangePasswordCommand implements CommandExecutor {
    private final MinecraftAuth plugin;
    private final AuthManager authManager;

    public ChangePasswordCommand(MinecraftAuth plugin, AuthManager authManager) {
        this.plugin = plugin;
        this.authManager = authManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c[MinecraftAuth] Este comando só pode ser usado por jogadores!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 3) {
            player.sendMessage("§c[MinecraftAuth] Uso: /changepassword <senha_atual> <nova_senha> <confirmar_nova_senha>");
            return true;
        }

        if (!authManager.isAuthenticated(player)) {
            player.sendMessage("§c[MinecraftAuth] Você precisa estar logado para alterar sua senha!");
            return true;
        }

        String currentPassword = args[0];
        String newPassword = args[1];
        String confirmNewPassword = args[2];

        // Executar alteração de senha de forma assíncrona
        authManager.changePassword(player, currentPassword, newPassword, confirmNewPassword).thenAccept(result -> {
            // Executar resposta no thread principal
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (result.isSuccess()) {
                    player.sendMessage("§a[MinecraftAuth] " + result.getMessage());
                } else {
                    player.sendMessage("§c[MinecraftAuth] " + result.getMessage());
                }
            });
        });

        return true;
    }
}