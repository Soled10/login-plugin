package com.example.authplugin.database;

import com.example.authplugin.AuthPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class YamlDatabase {
    
    private final AuthPlugin plugin;
    private final File playersFile;
    private final File sessionsFile;
    private FileConfiguration playersConfig;
    private FileConfiguration sessionsConfig;
    
    public YamlDatabase(AuthPlugin plugin) {
        this.plugin = plugin;
        
        // Criar diretório do plugin se não existir
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        
        // Definir arquivos
        this.playersFile = new File(dataFolder, "players.yml");
        this.sessionsFile = new File(dataFolder, "sessions.yml");
    }
    
    public boolean initialize() {
        try {
            // Criar arquivos se não existirem
            if (!playersFile.exists()) {
                playersFile.createNewFile();
            }
            if (!sessionsFile.exists()) {
                sessionsFile.createNewFile();
            }
            
            // Carregar configurações
            playersConfig = YamlConfiguration.loadConfiguration(playersFile);
            sessionsConfig = YamlConfiguration.loadConfiguration(sessionsFile);
            
            // Configurar headers dos arquivos
            setupPlayersFile();
            setupSessionsFile();
            
            plugin.getLogger().info("Banco de dados YAML inicializado com sucesso!");
            return true;
            
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao inicializar banco de dados YAML:", e);
            return false;
        }
    }
    
    private void setupPlayersFile() {
        if (playersConfig.getKeys(false).isEmpty()) {
            playersConfig.options().header(
                "# Arquivo de dados dos jogadores - AuthPlugin\n" +
                "# Estrutura:\n" +
                "# jogador:\n" +
                "#   password: hash_bcrypt_da_senha\n" +
                "#   uuid: uuid_do_jogador\n" +
                "#   premium: true/false\n" +
                "#   last-login: timestamp\n" +
                "#   registration-date: timestamp\n" +
                "#   last-ip: endereco_ip\n" +
                "#   login-attempts: numero_tentativas\n"
            );
            savePlayersConfig();
        }
    }
    
    private void setupSessionsFile() {
        if (sessionsConfig.getKeys(false).isEmpty()) {
            sessionsConfig.options().header(
                "# Arquivo de sessões ativas - AuthPlugin\n" +
                "# Estrutura:\n" +
                "# jogador:\n" +
                "#   uuid: uuid_do_jogador\n" +
                "#   ip-address: endereco_ip\n" +
                "#   login-time: timestamp\n" +
                "#   expires-at: timestamp\n"
            );
            saveSessionsConfig();
        }
    }
    
    public boolean registerPlayer(String username, String password, UUID uuid, boolean isPremium, String ip) {
        try {
            String playerPath = username.toLowerCase();
            
            // Verificar se já existe
            if (playersConfig.contains(playerPath)) {
                return false;
            }
            
            // Registrar dados
            playersConfig.set(playerPath + ".password", BCrypt.hashpw(password, BCrypt.gensalt()));
            playersConfig.set(playerPath + ".uuid", uuid != null ? uuid.toString() : null);
            playersConfig.set(playerPath + ".premium", isPremium);
            playersConfig.set(playerPath + ".last-login", System.currentTimeMillis());
            playersConfig.set(playerPath + ".registration-date", System.currentTimeMillis());
            playersConfig.set(playerPath + ".last-ip", ip);
            playersConfig.set(playerPath + ".login-attempts", 0);
            
            savePlayersConfig();
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Erro ao registrar jogador " + username + ":", e);
            return false;
        }
    }
    
    public boolean isPlayerRegistered(String username) {
        return playersConfig.contains(username.toLowerCase());
    }
    
    public boolean verifyPassword(String username, String password) {
        String playerPath = username.toLowerCase();
        
        if (!playersConfig.contains(playerPath)) {
            return false;
        }
        
        String hashedPassword = playersConfig.getString(playerPath + ".password");
        if (hashedPassword == null) {
            return false;
        }
        
        return BCrypt.checkpw(password, hashedPassword);
    }
    
    public boolean changePassword(String username, String newPassword) {
        try {
            String playerPath = username.toLowerCase();
            
            if (!playersConfig.contains(playerPath)) {
                return false;
            }
            
            playersConfig.set(playerPath + ".password", BCrypt.hashpw(newPassword, BCrypt.gensalt()));
            savePlayersConfig();
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Erro ao alterar senha do jogador " + username + ":", e);
            return false;
        }
    }
    
    public boolean isPremiumPlayer(String username) {
        String playerPath = username.toLowerCase();
        return playersConfig.getBoolean(playerPath + ".premium", false);
    }
    
    public void updateLastLogin(String username, String ip) {
        try {
            String playerPath = username.toLowerCase();
            
            if (playersConfig.contains(playerPath)) {
                playersConfig.set(playerPath + ".last-login", System.currentTimeMillis());
                playersConfig.set(playerPath + ".last-ip", ip);
                playersConfig.set(playerPath + ".login-attempts", 0);
                savePlayersConfig();
            }
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Erro ao atualizar último login do jogador " + username + ":", e);
        }
    }
    
    public int getLoginAttempts(String username) {
        String playerPath = username.toLowerCase();
        return playersConfig.getInt(playerPath + ".login-attempts", 0);
    }
    
    public void incrementLoginAttempts(String username) {
        try {
            String playerPath = username.toLowerCase();
            int currentAttempts = playersConfig.getInt(playerPath + ".login-attempts", 0);
            playersConfig.set(playerPath + ".login-attempts", currentAttempts + 1);
            savePlayersConfig();
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Erro ao incrementar tentativas de login do jogador " + username + ":", e);
        }
    }
    
    public void resetLoginAttempts(String username) {
        try {
            String playerPath = username.toLowerCase();
            playersConfig.set(playerPath + ".login-attempts", 0);
            savePlayersConfig();
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Erro ao resetar tentativas de login do jogador " + username + ":", e);
        }
    }
    
    public boolean createSession(String username, UUID uuid, String ip, long expiresAt) {
        try {
            // Remover sessão antiga primeiro
            removeSession(username);
            
            String playerPath = username.toLowerCase();
            sessionsConfig.set(playerPath + ".uuid", uuid != null ? uuid.toString() : null);
            sessionsConfig.set(playerPath + ".ip-address", ip);
            sessionsConfig.set(playerPath + ".login-time", System.currentTimeMillis());
            sessionsConfig.set(playerPath + ".expires-at", expiresAt);
            
            saveSessionsConfig();
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Erro ao criar sessão para jogador " + username + ":", e);
            return false;
        }
    }
    
    public boolean hasValidSession(String username, String ip) {
        String playerPath = username.toLowerCase();
        
        if (!sessionsConfig.contains(playerPath)) {
            return false;
        }
        
        String sessionIp = sessionsConfig.getString(playerPath + ".ip-address");
        long expiresAt = sessionsConfig.getLong(playerPath + ".expires-at", 0);
        
        return ip.equals(sessionIp) && System.currentTimeMillis() < expiresAt;
    }
    
    public void removeSession(String username) {
        try {
            String playerPath = username.toLowerCase();
            sessionsConfig.set(playerPath, null);
            saveSessionsConfig();
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Erro ao remover sessão do jogador " + username + ":", e);
        }
    }
    
    public void cleanExpiredSessions() {
        try {
            long currentTime = System.currentTimeMillis();
            int removed = 0;
            
            for (String player : sessionsConfig.getKeys(false)) {
                long expiresAt = sessionsConfig.getLong(player + ".expires-at", 0);
                
                if (currentTime >= expiresAt) {
                    sessionsConfig.set(player, null);
                    removed++;
                }
            }
            
            if (removed > 0) {
                saveSessionsConfig();
                plugin.getLogger().info("Removidas " + removed + " sessões expiradas.");
            }
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Erro ao limpar sessões expiradas:", e);
        }
    }
    
    public boolean unregisterPlayer(String username) {
        try {
            String playerPath = username.toLowerCase();
            
            if (!playersConfig.contains(playerPath)) {
                return false;
            }
            
            playersConfig.set(playerPath, null);
            savePlayersConfig();
            
            // Remover sessão também
            removeSession(username);
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Erro ao remover registro do jogador " + username + ":", e);
            return false;
        }
    }
    
    public String getPlayerUUID(String username) {
        String playerPath = username.toLowerCase();
        return playersConfig.getString(playerPath + ".uuid");
    }
    
    public void reloadData() {
        try {
            playersConfig = YamlConfiguration.loadConfiguration(playersFile);
            sessionsConfig = YamlConfiguration.loadConfiguration(sessionsFile);
            plugin.getLogger().info("Dados YAML recarregados com sucesso!");
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Erro ao recarregar dados YAML:", e);
        }
    }
    
    public int getTotalPlayers() {
        return playersConfig.getKeys(false).size();
    }
    
    public int getTotalSessions() {
        return sessionsConfig.getKeys(false).size();
    }
    
    public List<String> getRegisteredPlayers() {
        return playersConfig.getKeys(false).stream().toList();
    }
    
    private void savePlayersConfig() {
        try {
            playersConfig.save(playersFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao salvar arquivo de jogadores:", e);
        }
    }
    
    private void saveSessionsConfig() {
        try {
            sessionsConfig.save(sessionsFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao salvar arquivo de sessões:", e);
        }
    }
    
    public void close() {
        // Limpar sessões expiradas antes de fechar
        cleanExpiredSessions();
        plugin.getLogger().info("Banco de dados YAML fechado.");
    }
}