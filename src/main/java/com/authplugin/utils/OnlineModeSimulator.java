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

public class OnlineModeSimulator {
    
    private AuthPlugin plugin;
    
    public OnlineModeSimulator(AuthPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Simula online-mode=true para um jogador específico
     * Se passar, associa o UUID oficial da API Mojang ao jogador
     */
    public CompletableFuture<OnlineModeResult> simulateOnlineMode(String playerName, UUID currentUUID) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                plugin.getLogger().info("🔍 Simulando online-mode=true para: " + playerName);
                
                // Passo 1: Verifica se a conta existe na API Mojang
                String apiUUID = getPlayerUUIDFromAPI(playerName);
                
                if (apiUUID == null) {
                    plugin.getLogger().info("❌ Conta '" + playerName + "' não existe na API Mojang - FALHA no online-mode");
                    return new OnlineModeResult(false, null, "Conta não existe na API Mojang");
                }
                
                // Passo 2: Converte UUID da API para o formato correto
                UUID officialUUID = UUID.fromString(apiUUID);
                
                // Passo 3: Verifica se o nome já está registrado como conta original
                if (plugin.getDatabaseManager().isOriginalNameProtected(playerName)) {
                    UUID storedUUID = plugin.getDatabaseManager().getOriginalNameUUID(playerName);
                    
                    // Verifica se o UUID armazenado é o UUID oficial da API
                    // Se for, significa que é a conta original real
                    if (storedUUID != null && storedUUID.equals(officialUUID)) {
                        plugin.getLogger().info("✅ Conta original retornando: " + playerName);
                        plugin.getLogger().info("UUID atual: " + currentUUID);
                        plugin.getLogger().info("UUID registrado (oficial): " + storedUUID);
                        plugin.getLogger().info("UUID oficial (API): " + officialUUID);
                        return new OnlineModeResult(true, officialUUID, "Conta original retornando");
                    }
                    
                    // Se não for o UUID oficial, é uma conta pirata
                    plugin.getLogger().info("❌ Nome '" + playerName + "' já está registrado como conta original - BLOQUEANDO");
                    plugin.getLogger().info("UUID atual (pirata): " + currentUUID);
                    plugin.getLogger().info("UUID registrado (oficial): " + storedUUID);
                    plugin.getLogger().info("UUID oficial (API): " + officialUUID);
                    plugin.getLogger().info("Conta pirata tentando usar nome de conta original!");
                    return new OnlineModeResult(false, null, "Nome já registrado como conta original");
                }
                
                // Passo 4: Se chegou até aqui, é a primeira vez que este nome de conta original está sendo usado
                plugin.getLogger().info("✅ Primeira vez usando conta original: " + playerName);
                plugin.getLogger().info("✅ Simulando online-mode=true para usuário: " + playerName);
                plugin.getLogger().info("✅ Conta existe na API Mojang - USUÁRIO É PREMIUM");
                plugin.getLogger().info("UUID atual (offline): " + currentUUID);
                plugin.getLogger().info("UUID oficial (API): " + officialUUID);
                plugin.getLogger().info("Associando UUID oficial ao jogador: " + playerName);
                
                // Passo 4: Se chegou até aqui, é uma conta original válida
                plugin.getLogger().info("✅ Conta '" + playerName + "' passou na verificação online-mode");
                plugin.getLogger().info("UUID atual: " + currentUUID);
                plugin.getLogger().info("UUID oficial: " + officialUUID);
                plugin.getLogger().info("Associando UUID oficial ao jogador: " + playerName);
                
                return new OnlineModeResult(true, officialUUID, "Conta premium verificada com sucesso");
                
            } catch (Exception e) {
                plugin.getLogger().warning("Erro na simulação online-mode para " + playerName + ": " + e.getMessage());
                return new OnlineModeResult(false, null, "Erro na verificação: " + e.getMessage());
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
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("User-Agent", "AuthPlugin/1.0.0");
            
            int responseCode = connection.getResponseCode();
            plugin.getLogger().info("Resposta da API Mojang: " + responseCode + " para " + playerName);
            
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                String jsonResponse = response.toString();
                plugin.getLogger().info("Resposta JSON: " + jsonResponse);
                
                if (!jsonResponse.trim().isEmpty()) {
                    return extractUUIDFromJSON(jsonResponse);
                }
            } else if (responseCode == 204) {
                // 204 No Content significa que a conta não existe
                plugin.getLogger().info("Conta não encontrada (204): " + playerName);
                return null;
            } else if (responseCode == 503) {
                // 503 Service Unavailable - tenta novamente
                plugin.getLogger().warning("API Mojang indisponível (503) para " + playerName + " - tentando novamente...");
                return retryAPIRequest(playerName, 1);
            } else {
                plugin.getLogger().warning("Resposta inesperada da API Mojang: " + responseCode + " para " + playerName);
                return null;
            }
            
            return null;
        } catch (Exception e) {
            plugin.getLogger().warning("Erro ao obter UUID da API Mojang: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Tenta novamente a requisição da API em caso de erro 503
     */
    private String retryAPIRequest(String playerName, int attempt) {
        if (attempt > 3) {
            plugin.getLogger().warning("Máximo de tentativas atingido para " + playerName);
            return null;
        }
        
        try {
            plugin.getLogger().info("Tentativa " + attempt + " para " + playerName);
            Thread.sleep(2000 * attempt); // Espera progressiva
            
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("User-Agent", "AuthPlugin/1.0.0");
            
            int responseCode = connection.getResponseCode();
            plugin.getLogger().info("Tentativa " + attempt + " - Resposta: " + responseCode);
            
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                String jsonResponse = response.toString();
                plugin.getLogger().info("Tentativa " + attempt + " - JSON: " + jsonResponse);
                
                if (!jsonResponse.trim().isEmpty()) {
                    return extractUUIDFromJSON(jsonResponse);
                }
            } else if (responseCode == 503) {
                // Ainda indisponível, tenta novamente
                return retryAPIRequest(playerName, attempt + 1);
            }
            
            return null;
        } catch (Exception e) {
            plugin.getLogger().warning("Erro na tentativa " + attempt + " para " + playerName + ": " + e.getMessage());
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
    
    /**
     * Classe para armazenar resultado da simulação
     */
    public static class OnlineModeResult {
        private final boolean success;
        private final UUID officialUUID;
        private final String message;
        
        public OnlineModeResult(boolean success, UUID officialUUID, String message) {
            this.success = success;
            this.officialUUID = officialUUID;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public UUID getOfficialUUID() {
            return officialUUID;
        }
        
        public String getMessage() {
            return message;
        }
    }
}