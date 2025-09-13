package com.minecraftauth;

import com.minecraftauth.auth.AuthManager;
import com.minecraftauth.commands.AuthCommand;
import com.minecraftauth.commands.ChangePasswordCommand;
import com.minecraftauth.commands.LoginCommand;
import com.minecraftauth.commands.RegisterCommand;
import com.minecraftauth.database.DatabaseManager;
import com.minecraftauth.listeners.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

public class MinecraftAuth extends JavaPlugin {
    private DatabaseManager databaseManager;
    private AuthManager authManager;

    @Override
    public void onEnable() {
        // Salvar configuração padrão
        saveDefaultConfig();
        
        // Inicializar banco de dados
        databaseManager = new DatabaseManager(getConfig());
        
        // Inicializar gerenciador de autenticação
        authManager = new AuthManager(databaseManager, getConfig());
        
        // Registrar comandos
        registerCommands();
        
        // Registrar listeners
        registerListeners();
        
        getLogger().info("MinecraftAuth habilitado com sucesso!");
        getLogger().info("Sistema de autenticação ativo para online-mode=false");
    }

    @Override
    public void onDisable() {
        if (authManager != null) {
            authManager.cleanup();
        }
        
        if (databaseManager != null) {
            databaseManager.close();
        }
        
        getLogger().info("MinecraftAuth desabilitado!");
    }

    private void registerCommands() {
        getCommand("login").setExecutor(new LoginCommand(this, authManager));
        getCommand("register").setExecutor(new RegisterCommand(this, authManager));
        getCommand("changepassword").setExecutor(new ChangePasswordCommand(this, authManager));
        getCommand("auth").setExecutor(new AuthCommand(this, databaseManager));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this, authManager, databaseManager), this);
    }

    public AuthManager getAuthManager() {
        return authManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}