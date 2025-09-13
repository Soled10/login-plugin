package com.example.authplugin;

import com.example.authplugin.commands.*;
import com.example.authplugin.database.YamlDatabase;
import com.example.authplugin.listeners.PlayerListener;
import com.example.authplugin.managers.AuthManager;
import com.example.authplugin.managers.ConfigManager;
import com.example.authplugin.utils.MojangAPI;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class AuthPlugin extends JavaPlugin {
    
    private static AuthPlugin instance;
    private YamlDatabase yamlDatabase;
    private AuthManager authManager;
    private ConfigManager configManager;
    private MojangAPI mojangAPI;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Inicializar configuração
        configManager = new ConfigManager(this);
        
        // Inicializar banco de dados YAML
        yamlDatabase = new YamlDatabase(this);
        if (!yamlDatabase.initialize()) {
            getLogger().log(Level.SEVERE, "Falha ao inicializar banco de dados YAML! Desabilitando plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Inicializar API Mojang
        mojangAPI = new MojangAPI();
        
        // Inicializar gerenciador de autenticação
        authManager = new AuthManager(this);
        
        // Registrar eventos
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        
        // Registrar comandos
        registerCommands();
        
        getLogger().info("AuthPlugin habilitado com sucesso!");
        getLogger().info("Sistema de autenticação ativo para contas premium e piratas.");
        getLogger().info("Banco de dados: Arquivos YAML");
    }
    
    @Override
    public void onDisable() {
        if (yamlDatabase != null) {
            yamlDatabase.close();
        }
        getLogger().info("AuthPlugin desabilitado!");
    }
    
    private void registerCommands() {
        getCommand("register").setExecutor(new RegisterCommand(this));
        getCommand("login").setExecutor(new LoginCommand(this));
        getCommand("changepassword").setExecutor(new ChangePasswordCommand(this));
        getCommand("authreload").setExecutor(new AuthReloadCommand(this));
        getCommand("unregister").setExecutor(new UnregisterCommand(this));
    }
    
    public static AuthPlugin getInstance() {
        return instance;
    }
    
    public YamlDatabase getYamlDatabase() {
        return yamlDatabase;
    }
    
    public AuthManager getAuthManager() {
        return authManager;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public MojangAPI getMojangAPI() {
        return mojangAPI;
    }
}