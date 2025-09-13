package com.example.authplugin.managers;

import com.example.authplugin.AuthPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    
    private final AuthPlugin plugin;
    private final Map<String, Location> loginLocations;
    private final Map<String, BukkitTask> timeoutTasks;
    
    public SessionManager(AuthPlugin plugin) {
        this.plugin = plugin;
        this.loginLocations = new HashMap<>();
        this.timeoutTasks = new HashMap<>();
    }
    
    public void preparePlayer(Player player) {
        String playerName = player.getName().toLowerCase();
        
        // Salvar localização atual
        loginLocations.put(playerName, player.getLocation());
        
        // Aplicar efeitos de restrição
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 10, false, false));
        
        // Iniciar timeout de login
        startLoginTimeout(player);
        
        // Teleportar para spawn se configurado
        Location spawnLocation = getLoginSpawn();
        if (spawnLocation != null) {
            player.teleport(spawnLocation);
        }
    }
    
    public void releasePlayer(Player player) {
        String playerName = player.getName().toLowerCase();
        
        // Remover efeitos de restrição
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        player.removePotionEffect(PotionEffectType.SLOW);
        
        // Cancelar timeout
        cancelLoginTimeout(playerName);
        
        // Restaurar localização original se ainda não autenticado antes
        Location originalLocation = loginLocations.remove(playerName);
        if (originalLocation != null && !originalLocation.equals(player.getLocation())) {
            player.teleport(originalLocation);
        }
    }
    
    private void startLoginTimeout(Player player) {
        String playerName = player.getName().toLowerCase();
        int timeoutSeconds = plugin.getConfig().getInt("login-timeout", 60);
        
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    player.kickPlayer("§cTempo limite para fazer login esgotado!");
                }
                timeoutTasks.remove(playerName);
            }
        }.runTaskLater(plugin, timeoutSeconds * 20L);
        
        timeoutTasks.put(playerName, task);
    }
    
    private void cancelLoginTimeout(String playerName) {
        BukkitTask task = timeoutTasks.remove(playerName);
        if (task != null) {
            task.cancel();
        }
    }
    
    private Location getLoginSpawn() {
        String worldName = plugin.getConfig().getString("login-spawn.world");
        if (worldName == null) {
            return null;
        }
        
        double x = plugin.getConfig().getDouble("login-spawn.x", 0);
        double y = plugin.getConfig().getDouble("login-spawn.y", 100);
        double z = plugin.getConfig().getDouble("login-spawn.z", 0);
        float yaw = (float) plugin.getConfig().getDouble("login-spawn.yaw", 0);
        float pitch = (float) plugin.getConfig().getDouble("login-spawn.pitch", 0);
        
        return new Location(plugin.getServer().getWorld(worldName), x, y, z, yaw, pitch);
    }
    
    public void cleanupPlayer(String playerName) {
        loginLocations.remove(playerName.toLowerCase());
        cancelLoginTimeout(playerName.toLowerCase());
    }
}