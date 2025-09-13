package com.example.authplugin.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MojangAPI {
    
    private static final String MOJANG_API_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private static final String SESSION_SERVER_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    
    private final Map<String, UUID> uuidCache = new HashMap<>();
    private final Map<String, Long> cacheTime = new HashMap<>();
    private static final long CACHE_DURATION = 300000; // 5 minutos
    
    public UUID getUUIDFromUsername(String username) {
        String lowerUsername = username.toLowerCase();
        
        // Verificar cache
        if (uuidCache.containsKey(lowerUsername)) {
            Long cachedTime = cacheTime.get(lowerUsername);
            if (cachedTime != null && System.currentTimeMillis() - cachedTime < CACHE_DURATION) {
                return uuidCache.get(lowerUsername);
            }
        }
        
        try {
            URL url = new URL(MOJANG_API_URL + username);
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
                String uuidString = jsonObject.get("id").getAsString();
                
                // Formatar UUID
                UUID uuid = UUID.fromString(uuidString.replaceFirst(
                    "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                    "$1-$2-$3-$4-$5"
                ));
                
                // Cachear resultado
                uuidCache.put(lowerUsername, uuid);
                cacheTime.put(lowerUsername, System.currentTimeMillis());
                
                return uuid;
            } else if (responseCode == 404) {
                // Jogador não existe, cachear como null
                uuidCache.put(lowerUsername, null);
                cacheTime.put(lowerUsername, System.currentTimeMillis());
                return null;
            }
            
        } catch (IOException e) {
            // Em caso de erro, não cachear para tentar novamente depois
            return null;
        }
        
        return null;
    }
    
    public boolean isPremiumAccount(String username, UUID playerUuid) {
        // Primeiro verificar se o UUID corresponde ao username na Mojang
        UUID mojangUuid = getUUIDFromUsername(username);
        
        if (mojangUuid == null) {
            return false; // Username não existe na Mojang
        }
        
        // Verificar se o UUID do jogador corresponde ao UUID da Mojang
        return mojangUuid.equals(playerUuid);
    }
    
    public boolean hasValidSession(String username, UUID playerUuid) {
        try {
            String uuidString = playerUuid.toString().replace("-", "");
            URL url = new URL(SESSION_SERVER_URL + uuidString);
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
                String profileName = jsonObject.get("name").getAsString();
                
                return profileName.equalsIgnoreCase(username);
            }
            
        } catch (IOException e) {
            return false;
        }
        
        return false;
    }
    
    public void clearCache() {
        uuidCache.clear();
        cacheTime.clear();
    }
}