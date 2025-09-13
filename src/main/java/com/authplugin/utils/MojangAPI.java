package com.authplugin.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class MojangAPI {
    
    private static final String MOJANG_API_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private static final String MOJANG_SESSION_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    
    /**
     * Verifica se um jogador possui uma conta original do Minecraft
     * @param playerName Nome do jogador
     * @return true se a conta for original, false caso contrário
     */
    public boolean isOriginalAccount(String playerName) {
        try {
            URL url = new URL(MOJANG_API_URL + playerName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int responseCode = connection.getResponseCode();
            
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
                return jsonObject.has("id") && jsonObject.has("name");
            }
            
            return false;
        } catch (Exception e) {
            Bukkit.getLogger().warning("Erro ao verificar conta original: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtém o UUID de uma conta original
     * @param playerName Nome do jogador
     * @return UUID da conta ou null se não for original
     */
    public UUID getOriginalUUID(String playerName) {
        try {
            URL url = new URL(MOJANG_API_URL + playerName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int responseCode = connection.getResponseCode();
            
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
                if (jsonObject.has("id")) {
                    String uuidString = jsonObject.get("id").getAsString();
                    return UUID.fromString(formatUUID(uuidString));
                }
            }
            
            return null;
        } catch (Exception e) {
            Bukkit.getLogger().warning("Erro ao obter UUID original: " + e.getMessage());
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
    
    /**
     * Verifica se um UUID pertence a uma conta original
     * @param uuid UUID do jogador
     * @return true se for conta original, false caso contrário
     */
    public boolean isOriginalUUID(UUID uuid) {
        try {
            URL url = new URL(MOJANG_SESSION_URL + uuid.toString().replace("-", ""));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int responseCode = connection.getResponseCode();
            return responseCode == 200;
        } catch (Exception e) {
            Bukkit.getLogger().warning("Erro ao verificar UUID original: " + e.getMessage());
            return false;
        }
    }
}