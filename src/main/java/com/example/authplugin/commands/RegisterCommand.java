package com.example.authplugin.commands;

import com.example.authplugin.AuthPlugin;
import com.example.authplugin.utils.MojangAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RegisterCommand implements CommandExecutor {
    
    private final AuthPlugin plugin;
    
    public RegisterCommand(AuthPlugin plugin) {
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
        
        // Verificar se já está autenticado
        if (plugin.getAuthManager().isAuthenticated(player.getUniqueId())) {
            player.sendMessage(plugin.getConfigManager().getMessage("register-already"));
            return true;
        }
        
        // Verificar se já está registrado
        if (plugin.getAuthManager().isRegistered(username)) {
            player.sendMessage(plugin.getConfigManager().getMessage("register-already"));
            return true;
        }
        
        // Verificar argumentos
        if (args.length != 2) {
            player.sendMessage(plugin.getConfigManager().getMessage("register-usage"));
            return true;
        }
        
        String password = args[0];
        String confirmPassword = args[1];
        
        // Verificar se as senhas coincidem
        if (!password.equals(confirmPassword)) {
            player.sendMessage(plugin.getConfigManager().getMessage("register-password-mismatch"));
            return true;
        }
        
        // Verificar tamanho mínimo da senha
        if (password.length() < 6) {
            player.sendMessage(plugin.getConfigManager().getMessage("register-password-short"));
            return true;
        }
        
        // Verificar assincronamente se é uma conta premium (para proteção)
        new BukkitRunnable() {
            @Override
            public void run() {
                MojangAPI.PremiumInfo premiumInfo = plugin.getMojangAPI().checkPremium(username);
                
                // Voltar para thread principal para interagir com o jogador
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // Verificar se ainda está online
                        if (!player.isOnline()) {
                            return;
                        }
                        
                        // Bloquear piratas de usar nomes de contas premium
                        if (premiumInfo.isPremium() && plugin.getConfigManager().isBlockPremiumNames()) {
                            if (!player.getUniqueId().equals(premiumInfo.getUuid())) {
                                player.sendMessage(plugin.getConfigManager().getMessage("register-premium-block"));
                                return;
                            }
                        }
                        
                        // Tentar registrar o jogador
                        if (plugin.getAuthManager().registerPlayer(player, password)) {
                            player.sendMessage(plugin.getConfigManager().getMessage("register-success"));
                            plugin.getLogger().info("Jogador " + username + " se registrou com sucesso.");
                        } else {
                            player.sendMessage("§cErro ao registrar. Tente novamente.");
                            plugin.getLogger().warning("Falha ao registrar jogador " + username);
                        }
                    }
                }.runTask(plugin);
            }
        }.runTaskAsynchronously(plugin);
        
        return true;
    }
}