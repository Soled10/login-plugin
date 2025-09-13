package com.example.authplugin;

import com.example.authplugin.commands.ChangePasswordCommand;
import com.example.authplugin.commands.LoginCommand;
import com.example.authplugin.commands.RegisterCommand;
import com.example.authplugin.database.DatabaseManager;
import com.example.authplugin.listeners.PlayerListener;
import com.example.authplugin.managers.AuthManager;
import com.example.authplugin.managers.SessionManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AuthPlugin extends JavaPlugin {
    
    private DatabaseManager databaseManager;
    private AuthManager authManager;
    private SessionManager sessionManager;
    
    @Override
    public void onEnable() {
        // Criar config padrão se não existir
        saveDefaultConfig();
        
        // Inicializar database
        databaseManager = new DatabaseManager(this);
        databaseManager.initialize();
        
        // Inicializar managers
        authManager = new AuthManager(this, databaseManager);
        sessionManager = new SessionManager(this);
        
        // Registrar comandos
        getCommand("login").setExecutor(new LoginCommand(this, authManager));
        getCommand("register").setExecutor(new RegisterCommand(this, authManager));
        getCommand("changepassword").setExecutor(new ChangePasswordCommand(this, authManager));
        
        // Registrar listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(this, authManager, sessionManager), this);
        
        getLogger().info("AuthPlugin habilitado com sucesso!");
    }
    
    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }
        getLogger().info("AuthPlugin desabilitado!");
    }
    
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    public AuthManager getAuthManager() {
        return authManager;
    }
    
    public SessionManager getSessionManager() {
        return sessionManager;
    }
}