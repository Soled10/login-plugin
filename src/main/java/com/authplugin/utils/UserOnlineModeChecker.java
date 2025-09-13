package com.authplugin.utils;

import com.authplugin.AuthPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserOnlineModeChecker {
    
    private AuthPlugin plugin;
    
    public UserOnlineModeChecker(AuthPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Simula verificação de online-mode=true para um usuário específico
     * Verifica se a conta existe na API da Mojang e se o UUID coincide
     */
    public CompletableFuture<Boolean> verifyUserOnlineMode(String playerName, UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                plugin.getLogger().info("🔍 Simulando online-mode=true para usuário: " + playerName);
                
                // Passo 1: Verifica se a conta existe na API Mojang
                String apiUUID = getPlayerUUIDFromAPI(playerName);
                
                if (apiUUID == null) {
                    plugin.getLogger().info("❌ Conta '" + playerName + "' não existe na API Mojang - FALHA no online-mode");
                    return false;
                }
                
                // Passo 2: Converte UUID da API para o formato correto
                UUID apiUUIDFormatted = UUID.fromString(apiUUID);
                
                // Passo 3: Verifica se o UUID da API coincide com o UUID do jogador
                // Em online-mode=true, o UUID seria o mesmo da API
                if (apiUUIDFormatted.equals(playerUUID)) {
                    plugin.getLogger().info("✅ Conta '" + playerName + "' passou na verificação online-mode - PREMIUM");
                    return true;
                } else {
                    plugin.getLogger().info("❌ UUID da API não coincide com UUID do jogador - FALHA no online-mode");
                    plugin.getLogger().info("API UUID: " + apiUUIDFormatted);
                    plugin.getLogger().info("Player UUID: " + playerUUID);
                    return false;
                }
                
            } catch (Exception e) {
                plugin.getLogger().warning("Erro na verificação online-mode para " + playerName + ": " + e.getMessage());
                return false;
            }
        });
    }
    
    /**
     * Obtém o UUID de um jogador via API da Mojang
     */
    private String getPlayerUUIDFromAPI(String playerName) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("User-Agent", "AuthPlugin/1.0.0");
            
            int responseCode = connection.getResponseCode();
            
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                String jsonResponse = response.toString();
                if (!jsonResponse.trim().isEmpty()) {
                    return extractUUIDFromJSON(jsonResponse);
                }
            } else if (responseCode == 204) {
                // 204 No Content significa que a conta não existe
                return null;
            }
            
            return null;
        } catch (Exception e) {
            plugin.getLogger().warning("Erro ao obter UUID da API Mojang: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Extrai o UUID do JSON da resposta da API
     */
    private String extractUUIDFromJSON(String json) {
        try {
            // Procura por "id" : "uuid"
            int idIndex = json.indexOf("\"id\"");
            if (idIndex != -1) {
                int startIndex = json.indexOf("\"", idIndex + 4) + 1;
                int endIndex = json.indexOf("\"", startIndex);
                if (startIndex > 0 && endIndex > startIndex) {
                    String uuid = json.substring(startIndex, endIndex);
                    // Formata o UUID para o formato correto
                    return formatUUID(uuid);
                }
            }
            return null;
        } catch (Exception e) {
            plugin.getLogger().warning("Erro ao extrair UUID do JSON: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Formata UUID para o formato correto
     */
    private String formatUUID(String uuid) {
        if (uuid.length() == 32) {
            return uuid.substring(0, 8) + "-" + 
                   uuid.substring(8, 12) + "-" + 
                   uuid.substring(12, 16) + "-" + 
                   uuid.substring(16, 20) + "-" + 
                   uuid.substring(20, 32);
        }
        return uuid;
    }
}