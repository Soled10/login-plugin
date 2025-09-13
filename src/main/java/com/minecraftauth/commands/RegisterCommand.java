package com.minecraftauth.commands;

import com.minecraftauth.MinecraftAuth;
import com.minecraftauth.auth.AuthManager;
import com.minecraftauth.auth.AuthResult;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegisterCommand implements CommandExecutor {
    private final MinecraftAuth plugin;
    private final AuthManager authManager;

    public RegisterCommand(MinecraftAuth plugin, AuthManager authManager) {
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

        if (args.length != 2) {
            player.sendMessage("§c[MinecraftAuth] Uso: /register <senha> <confirmar_senha>");
            return true;
        }

        if (authManager.isAuthenticated(player)) {
            player.sendMessage("§c[MinecraftAuth] Você já está logado!");
            return true;
        }

        String password = args[0];
        String confirmPassword = args[1];

        // Executar registro de forma assíncrona
        authManager.registerPlayer(player, password, confirmPassword).thenAccept(result -> {
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