package com.minecraftauth.listeners;

import com.minecraftauth.MinecraftAuth;
import com.minecraftauth.auth.AuthManager;
import com.minecraftauth.database.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.concurrent.CompletableFuture;

public class PlayerListener implements Listener {
    private final MinecraftAuth plugin;
    private final AuthManager authManager;
    private final DatabaseManager databaseManager;

    public PlayerListener(MinecraftAuth plugin, AuthManager authManager, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.authManager = authManager;
        this.databaseManager = databaseManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        String username = event.getName();
        
        // Verificar se o username é de uma conta original
        try {
            boolean isOriginalAccount = databaseManager.isOriginalAccount(username).get();
            
            if (isOriginalAccount) {
                // Verificar se o UUID corresponde ao da conta original
                boolean isCorrectUUID = databaseManager.isOriginalAccount(event.getUniqueId()).get();
                
                if (!isCorrectUUID) {
                    // Tentativa de usar nick de conta original com UUID diferente
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, 
                        ChatColor.RED + "Este nome de usuário pertence a uma conta original!\n" +
                        ChatColor.YELLOW + "Use seu próprio nome de usuário para jogar.");
                    return;
                }
            }
            
        } catch (Exception e) {
            plugin.getLogger().severe("Erro ao verificar conta original: " + e.getMessage());
            // Em caso de erro, permitir login para não quebrar o servidor
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Verificar se é conta original
        try {
            boolean isOriginalAccount = databaseManager.isOriginalAccount(player.getName()).get();
            
            if (isOriginalAccount) {
                // Login automático para contas originais
                authManager.authenticatePlayer(player, "").thenAccept(result -> {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        if (result.isSuccess()) {
                            player.sendMessage("§a[MinecraftAuth] §eLogin automático realizado!");
                        } else {
                            player.sendMessage("§c[MinecraftAuth] " + result.getMessage());
                        }
                    });
                });
            } else {
                // Conta pirata - mostrar mensagem de login/registro
                player.sendMessage("§e[MinecraftAuth] Bem-vindo ao servidor!");
                player.sendMessage("§e[MinecraftAuth] Use /login <senha> para fazer login ou /register <senha> <confirmar_senha> para se registrar.");
                
                // Aplicar restrições
                applyRestrictions(player);
            }
            
        } catch (Exception e) {
            plugin.getLogger().severe("Erro ao processar login: " + e.getMessage());
            applyRestrictions(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        authManager.logout(player);
    }

    private void applyRestrictions(Player player) {
        // Aplicar efeitos visuais para indicar que precisa fazer login
        player.setWalkSpeed(0.0f);
        player.setFlySpeed(0.0f);
        player.setAllowFlight(false);
        player.setFlying(false);
    }

    private void removeRestrictions(Player player) {
        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.1f);
    }

    // Bloquear comandos para jogadores não autenticados
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        
        if (player.hasPermission("minecraftauth.admin")) {
            return; // Admins podem usar comandos
        }
        
        if (!authManager.isAuthenticated(player)) {
            String command = event.getMessage().toLowerCase();
            
            // Permitir apenas comandos de autenticação
            if (!command.startsWith("/login") && 
                !command.startsWith("/register") && 
                !command.startsWith("/changepassword")) {
                event.setCancelled(true);
                player.sendMessage("§c[MinecraftAuth] Você precisa fazer login primeiro! Use /login <senha> ou /register <senha> <confirmar_senha>");
            }
        }
    }

    // Bloquear movimento para jogadores não autenticados
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        if (!authManager.isAuthenticated(player) && !player.hasPermission("minecraftauth.admin")) {
            event.setCancelled(true);
        }
    }

    // Bloquear chat para jogadores não autenticados
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        
        if (!authManager.isAuthenticated(player) && !player.hasPermission("minecraftauth.admin")) {
            event.setCancelled(true);
            player.sendMessage("§c[MinecraftAuth] Você precisa fazer login primeiro!");
        }
    }

    // Bloquear interações para jogadores não autenticados
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if (!authManager.isAuthenticated(player) && !player.hasPermission("minecraftauth.admin")) {
            event.setCancelled(true);
        }
    }

    // Bloquear quebra de blocos para jogadores não autenticados
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        
        if (!authManager.isAuthenticated(player) && !player.hasPermission("minecraftauth.admin")) {
            event.setCancelled(true);
        }
    }

    // Bloquear colocação de blocos para jogadores não autenticados
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        
        if (!authManager.isAuthenticated(player) && !player.hasPermission("minecraftauth.admin")) {
            event.setCancelled(true);
        }
    }

    // Bloquear drop de itens para jogadores não autenticados
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        
        if (!authManager.isAuthenticated(player) && !player.hasPermission("minecraftauth.admin")) {
            event.setCancelled(true);
        }
    }

    // Bloquear coleta de itens para jogadores não autenticados
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        
        if (!authManager.isAuthenticated(player) && !player.hasPermission("minecraftauth.admin")) {
            event.setCancelled(true);
        }
    }

    // Bloquear abertura de inventários para jogadores não autenticados
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            
            if (!authManager.isAuthenticated(player) && !player.hasPermission("minecraftauth.admin")) {
                event.setCancelled(true);
            }
        }
    }

    // Bloquear cliques em inventários para jogadores não autenticados
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            
            if (!authManager.isAuthenticated(player) && !player.hasPermission("minecraftauth.admin")) {
                event.setCancelled(true);
            }
        }
    }
}