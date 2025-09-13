package com.authplugin.listeners;

import com.authplugin.AuthPlugin;
import com.authplugin.utils.AuthUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerCommandListener implements Listener {
    
    private AuthPlugin plugin;
    private AuthUtils authUtils;
    
    // Comandos permitidos para jogadores não autenticados
    private static final String[] ALLOWED_COMMANDS = {
        "login", "register", "changepassword", "auth"
    };
    
    public PlayerCommandListener(AuthPlugin plugin) {
        this.plugin = plugin;
        this.authUtils = plugin.getAuthUtils();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        
        // Se o jogador estiver autenticado, permite todos os comandos
        if (plugin.isPlayerAuthenticated(player.getUniqueId())) {
            return;
        }
        
        // Se não estiver autenticado, verifica se o comando é permitido
        String command = event.getMessage().toLowerCase().substring(1); // Remove a barra inicial
        
        // Extrai o comando principal (antes do primeiro espaço)
        String mainCommand = command.split(" ")[0];
        
        // Verifica se o comando é permitido
        boolean isAllowed = false;
        for (String allowedCommand : ALLOWED_COMMANDS) {
            if (mainCommand.equals(allowedCommand)) {
                isAllowed = true;
                break;
            }
        }
        
        // Se o comando não for permitido, cancela e envia mensagem
        if (!isAllowed) {
            event.setCancelled(true);
            authUtils.sendErrorMessage(player, "Você precisa estar autenticado para usar este comando!");
            authUtils.sendInfoMessage(player, "Use /login <senha> ou /register <senha> <confirmar_senha> para se autenticar.");
        }
    }
}