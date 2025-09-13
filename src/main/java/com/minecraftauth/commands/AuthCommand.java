package com.minecraftauth.commands;

import com.minecraftauth.MinecraftAuth;
import com.minecraftauth.database.DatabaseManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class AuthCommand implements CommandExecutor {
    private final MinecraftAuth plugin;
    private final DatabaseManager databaseManager;

    public AuthCommand(MinecraftAuth plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("minecraftauth.admin")) {
            sender.sendMessage("§c[MinecraftAuth] Você não tem permissão para usar este comando!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§c[MinecraftAuth] Uso: /auth <reload|stats|addoriginal>");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "reload":
                handleReload(sender);
                break;
            case "stats":
                handleStats(sender);
                break;
            case "addoriginal":
                handleAddOriginal(sender, args);
                break;
            default:
                sender.sendMessage("§c[MinecraftAuth] Subcomando inválido! Use: reload, stats ou addoriginal");
                break;
        }

        return true;
    }

    private void handleReload(CommandSender sender) {
        try {
            plugin.reloadConfig();
            sender.sendMessage("§a[MinecraftAuth] Configuração recarregada com sucesso!");
        } catch (Exception e) {
            sender.sendMessage("§c[MinecraftAuth] Erro ao recarregar configuração: " + e.getMessage());
        }
    }

    private void handleStats(CommandSender sender) {
        sender.sendMessage("§e[MinecraftAuth] Estatísticas do servidor:");
        sender.sendMessage("§e- Jogadores online: §f" + plugin.getServer().getOnlinePlayers().size());
        sender.sendMessage("§e- Versão do servidor: §f" + plugin.getServer().getVersion());
        sender.sendMessage("§e- Versão do plugin: §f" + plugin.getDescription().getVersion());
    }

    private void handleAddOriginal(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("§c[MinecraftAuth] Uso: /auth addoriginal <username>");
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("§c[MinecraftAuth] Este comando só pode ser usado por jogadores!");
            return;
        }

        Player player = (Player) sender;
        String username = args[1];

        // Adicionar conta original
        CompletableFuture<Boolean> future = databaseManager.addOriginalAccount(username, player.getUniqueId());
        future.thenAccept(success -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (success) {
                    sender.sendMessage("§a[MinecraftAuth] Conta original '" + username + "' adicionada com sucesso!");
                } else {
                    sender.sendMessage("§c[MinecraftAuth] Erro ao adicionar conta original!");
                }
            });
        });
    }
}