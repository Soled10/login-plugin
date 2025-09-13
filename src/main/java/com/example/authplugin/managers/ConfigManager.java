package com.example.authplugin.managers;

import com.example.authplugin.AuthPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    
    private final AuthPlugin plugin;
    private FileConfiguration config;
    
    public ConfigManager(AuthPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }
    
    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }
    
    // Configurações de segurança
    public boolean isPremiumAutoLogin() {
        return config.getBoolean("security.premium-auto-login", true);
    }
    
    public boolean isCheckPremiumUUID() {
        return config.getBoolean("security.check-premium-uuid", true);
    }
    
    public boolean isBlockPremiumNames() {
        return config.getBoolean("security.block-premium-names", true);
    }
    
    public int getSessionTimeout() {
        return config.getInt("security.session-timeout", 300); // 5 minutos
    }
    
    public int getMaxLoginAttempts() {
        return config.getInt("security.max-login-attempts", 3);
    }
    
    public int getLoginTimeout() {
        return config.getInt("security.login-timeout", 60); // 60 segundos
    }
    
    // Configurações do banco de dados
    public String getDatabasePath() {
        return config.getString("database.path", "plugins/AuthPlugin/");
    }
    
    // Mensagens
    public String getMessage(String key) {
        return config.getString("messages." + key, "Mensagem não encontrada: " + key);
    }
    
    // Configurações de spawn
    public boolean isTeleportToSpawn() {
        return config.getBoolean("spawn.teleport-to-spawn", true);
    }
    
    public String getSpawnWorld() {
        return config.getString("spawn.world", "world");
    }
    
    public double getSpawnX() {
        return config.getDouble("spawn.x", 0);
    }
    
    public double getSpawnY() {
        return config.getDouble("spawn.y", 64);
    }
    
    public double getSpawnZ() {
        return config.getDouble("spawn.z", 0);
    }
    
    public float getSpawnYaw() {
        return (float) config.getDouble("spawn.yaw", 0);
    }
    
    public float getSpawnPitch() {
        return (float) config.getDouble("spawn.pitch", 0);
    }
}