package com.example.authplugin.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MojangAPI {
    
    private static final String MOJANG_API_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private static final String MOJANG_UUID_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final Logger logger = Logger.getLogger("AuthPlugin");
    
    // Cache para evitar muitas requisições à API
    private final Map<String, PremiumInfo> premiumCache = new ConcurrentHashMap<>();
    private final Map<String, Long> cacheTimestamps = new ConcurrentHashMap<>();
    private static final long CACHE_DURATION = 300000; // 5 minutos em millisegundos
    
    public static class PremiumInfo {
        private final boolean isPremium;
        private final UUID uuid;
        private final String correctName;
        
        public PremiumInfo(boolean isPremium, UUID uuid, String correctName) {
            this.isPremium = isPremium;
            this.uuid = uuid;
            this.correctName = correctName;
        }
        
        public boolean isPremium() {
            return isPremium;
        }
        
        public UUID getUuid() {
            return uuid;
        }
        
        public String getCorrectName() {
            return correctName;
        }
    }
    
    /**
     * Verifica se um nome de usuário é uma conta premium (assíncrono)
     */
    public CompletableFuture<PremiumInfo> checkPremiumAsync(String username) {
        return CompletableFuture.supplyAsync(() -> {
            // Verificar cache primeiro
            String lowerUsername = username.toLowerCase();
            Long cacheTime = cacheTimestamps.get(lowerUsername);
            
            if (cacheTime != null && (System.currentTimeMillis() - cacheTime) < CACHE_DURATION) {
                PremiumInfo cached = premiumCache.get(lowerUsername);
                if (cached != null) {
                    return cached;
                }
            }
            
            try {
                // Fazer requisição à API Mojang
                URL url = new URL(MOJANG_API_URL + username);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestProperty("User-Agent", "AuthPlugin/1.0");
                
                int responseCode = connection.getResponseCode();
                
                if (responseCode == 200) {
                    // Conta premium encontrada
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    JSONObject json = new JSONObject(response.toString());
                    String id = json.getString("id");
                    String name = json.getString("name");
                    
                    // Converter ID para UUID
                    UUID uuid = parseUUID(id);
                    
                    PremiumInfo info = new PremiumInfo(true, uuid, name);
                    
                    // Armazenar no cache
                    premiumCache.put(lowerUsername, info);
                    cacheTimestamps.put(lowerUsername, System.currentTimeMillis());
                    
                    return info;
                    
                } else if (responseCode == 404) {
                    // Conta não premium
                    PremiumInfo info = new PremiumInfo(false, null, username);
                    
                    // Armazenar no cache
                    premiumCache.put(lowerUsername, info);
                    cacheTimestamps.put(lowerUsername, System.currentTimeMillis());
                    
                    return info;
                    
                } else {
                    logger.warning("API Mojang retornou código " + responseCode + " para usuário " + username);
                    // Em caso de erro, assumir não premium por segurança
                    return new PremiumInfo(false, null, username);
                }
                
            } catch (IOException e) {
                logger.log(Level.WARNING, "Erro ao verificar conta premium para " + username + ":", e);
                // Em caso de erro, assumir não premium por segurança
                return new PremiumInfo(false, null, username);
            }
        });
    }
    
    /**
     * Verifica se um nome de usuário é uma conta premium (síncrono)
     */
    public PremiumInfo checkPremium(String username) {
        try {
            return checkPremiumAsync(username).get();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Erro ao verificar conta premium para " + username + ":", e);
            return new PremiumInfo(false, null, username);
        }
    }
    
    /**
     * Verifica se um UUID corresponde a uma conta premium
     */
    public CompletableFuture<Boolean> verifyUUIDAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String uuidString = uuid.toString().replace("-", "");
                URL url = new URL(MOJANG_UUID_URL + uuidString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestProperty("User-Agent", "AuthPlugin/1.0");
                
                int responseCode = connection.getResponseCode();
                return responseCode == 200;
                
            } catch (IOException e) {
                logger.log(Level.WARNING, "Erro ao verificar UUID " + uuid + ":", e);
                return false;
            }
        });
    }
    
    /**
     * Converte string de ID da API Mojang para UUID
     */
    private UUID parseUUID(String id) {
        if (id.length() != 32) {
            throw new IllegalArgumentException("ID deve ter 32 caracteres");
        }
        
        String formatted = id.substring(0, 8) + "-" + 
                          id.substring(8, 12) + "-" + 
                          id.substring(12, 16) + "-" + 
                          id.substring(16, 20) + "-" + 
                          id.substring(20, 32);
        
        return UUID.fromString(formatted);
    }
    
    /**
     * Limpa o cache de contas premium
     */
    public void clearCache() {
        premiumCache.clear();
        cacheTimestamps.clear();
        logger.info("Cache da API Mojang limpo.");
    }
    
    /**
     * Remove entradas antigas do cache
     */
    public void cleanCache() {
        long currentTime = System.currentTimeMillis();
        cacheTimestamps.entrySet().removeIf(entry -> {
            if ((currentTime - entry.getValue()) > CACHE_DURATION) {
                premiumCache.remove(entry.getKey());
                return true;
            }
            return false;
        });
    }
}