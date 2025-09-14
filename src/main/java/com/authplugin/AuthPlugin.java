package com.authplugin;

import com.authplugin.commands.AuthCommand;
import com.authplugin.commands.ChangePasswordCommand;
import com.authplugin.commands.LoginCommand;
import com.authplugin.commands.RegisterCommand;
import com.authplugin.database.DatabaseManager;
import com.authplugin.listeners.PlayerJoinListener;
import com.authplugin.listeners.PlayerMoveListener;
import com.authplugin.listeners.PlayerChatListener;
import com.authplugin.listeners.PlayerCommandListener;
import com.authplugin.listeners.PlayerInteractListener;
import com.authplugin.listeners.PlayerDamageListener;
import com.authplugin.utils.AuthUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthPlugin extends JavaPlugin {
    
    private static AuthPlugin instance;
    private DatabaseManager databaseManager;
    private AuthUtils authUtils;
    
    // Armazena jogadores que estão logados
    private Map<UUID, Boolean> loggedInPlayers;
    
    // Armazena jogadores que estão autenticados (originais ou piratas)
    private Map<UUID, Boolean> authenticatedPlayers;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Inicializa mapas
        loggedInPlayers = new HashMap<>();
        authenticatedPlayers = new HashMap<>();
        
        // Inicializa componentes
        databaseManager = new DatabaseManager(this);
        authUtils = new AuthUtils(this);
        
        // Inicializa banco de dados
        databaseManager.initialize();
        
        // Registra comandos
        registerCommands();
        
        // Registra listeners
        registerListeners();
        
        getLogger().info("AuthPlugin habilitado com sucesso!");
    }
    
    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }
        getLogger().info("AuthPlugin desabilitado!");
    }
    
    private void registerCommands() {
        getCommand("register").setExecutor(new RegisterCommand(this));
        getCommand("login").setExecutor(new LoginCommand(this));
        getCommand("changepassword").setExecutor(new ChangePasswordCommand(this));
        getCommand("auth").setExecutor(new AuthCommand(this));
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerCommandListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDamageListener(this), this);
    }
    
    // Getters
    public static AuthPlugin getInstance() {
        return instance;
    }
    
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    public AuthUtils getAuthUtils() {
        return authUtils;
    }
    
    
    public Map<UUID, Boolean> getLoggedInPlayers() {
        return loggedInPlayers;
    }
    
    public Map<UUID, Boolean> getAuthenticatedPlayers() {
        return authenticatedPlayers;
    }
    
    public boolean isPlayerLoggedIn(UUID uuid) {
        return loggedInPlayers.getOrDefault(uuid, false);
    }
    
    public boolean isPlayerAuthenticated(UUID uuid) {
        return authenticatedPlayers.getOrDefault(uuid, false);
    }
    
    public void setPlayerLoggedIn(UUID uuid, boolean loggedIn) {
        loggedInPlayers.put(uuid, loggedIn);
    }
    
    public void setPlayerAuthenticated(UUID uuid, boolean authenticated) {
        authenticatedPlayers.put(uuid, authenticated);
    }
}