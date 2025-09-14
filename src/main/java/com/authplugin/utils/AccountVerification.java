package com.authplugin.utils;

import com.authplugin.AuthPlugin;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AccountVerification {
    
    private AuthPlugin plugin;
    
    public AccountVerification(AuthPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Verifica se uma conta é original usando múltiplos métodos
     */
    public CompletableFuture<VerificationResult> verifyAccount(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            String playerName = player.getName();
            UUID playerUUID = player.getUniqueId();
            
            plugin.getLogger().info("🔍 Verificação multicamada para: " + playerName);
            
            // Método 1: Verificação por API Mojang
            boolean mojangAPIResult = verifyByMojangAPI(playerName);
            plugin.getLogger().info("📡 API Mojang: " + (mojangAPIResult ? "✅ VÁLIDA" : "❌ INVÁLIDA"));
            
            // Método 2: Verificação por UUID version
            boolean uuidVersionResult = verifyByUUIDVersion(playerUUID);
            plugin.getLogger().info("🆔 Versão UUID: " + (uuidVersionResult ? "✅ VÁLIDA" : "❌ INVÁLIDA"));
            
            // Método 3: Verificação por padrão de UUID
            boolean uuidPatternResult = verifyByUUIDPattern(playerUUID);
            plugin.getLogger().info("🔍 Padrão UUID: " + (uuidPatternResult ? "✅ VÁLIDA" : "❌ INVÁLIDA"));
            
            // Método 4: Verificação por comportamento do cliente
            boolean clientBehaviorResult = verifyByClientBehavior(player);
            plugin.getLogger().info("💻 Comportamento: " + (clientBehaviorResult ? "✅ VÁLIDA" : "❌ INVÁLIDA"));
            
            // Método 5: Verificação por histórico de conexão
            boolean connectionHistoryResult = verifyByConnectionHistory(player);
            plugin.getLogger().info("📊 Histórico: " + (connectionHistoryResult ? "✅ VÁLIDA" : "❌ INVÁLIDA"));
            
            // Calcula pontuação de confiança
            int confidenceScore = calculateConfidenceScore(
                mojangAPIResult, uuidVersionResult, uuidPatternResult, 
                clientBehaviorResult, connectionHistoryResult
            );
            
            plugin.getLogger().info("🎯 Pontuação de confiança: " + confidenceScore + "/100");
            
            // Determina se é conta original baseado na pontuação
            boolean isOriginal = confidenceScore >= 70; // 70% de confiança mínima
            
            if (isOriginal) {
                plugin.getLogger().info("✅ CONTA ORIGINAL CONFIRMADA: " + playerName);
                return new VerificationResult(true, "Conta original verificada", confidenceScore);
            } else {
                plugin.getLogger().info("❌ CONTA PIRATA DETECTADA: " + playerName);
                return new VerificationResult(false, "Conta pirata detectada", confidenceScore);
            }
        });
    }
    
    /**
     * Método 1: Verificação por API Mojang
     */
    private boolean verifyByMojangAPI(String playerName) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("User-Agent", "AuthPlugin/1.0.0");
            
            int responseCode = connection.getResponseCode();
            return responseCode == 200;
        } catch (Exception e) {
            plugin.getLogger().warning("Erro na verificação API Mojang: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Método 2: Verificação por versão do UUID
     */
    private boolean verifyByUUIDVersion(UUID uuid) {
        int version = uuid.version();
        
        // UUIDs v4 são mais comuns para contas originais
        // UUIDs v3 são gerados offline (contas piratas)
        if (version == 4) {
            return true; // Provável conta original
        } else if (version == 3) {
            return false; // Provável conta pirata
        }
        
        return false; // Outras versões são suspeitas
    }
    
    /**
     * Método 3: Verificação por padrão do UUID
     */
    private boolean verifyByUUIDPattern(UUID uuid) {
        String uuidString = uuid.toString();
        
        // Verifica se o UUID segue padrões comuns de contas originais
        // Contas originais tendem a ter UUIDs com certos padrões
        
        // Verifica se não é um UUID gerado por algoritmo offline
        if (uuidString.startsWith("00000000-0000-3000-8000-")) {
            return false; // UUID offline típico
        }
        
        // Verifica se tem variação suficiente (não é sequencial)
        String[] parts = uuidString.split("-");
        boolean hasVariation = false;
        
        for (String part : parts) {
            if (!part.matches("^[0-9a-f]{4}$")) {
                return false; // Formato inválido
            }
            
            // Verifica se não é apenas zeros ou padrões simples
            if (!part.equals("0000") && !part.matches("^[0-9a-f]{1}[0-9a-f]{3}$")) {
                hasVariation = true;
            }
        }
        
        return hasVariation;
    }
    
    /**
     * Método 4: Verificação por comportamento do cliente
     */
    private boolean verifyByClientBehavior(Player player) {
        try {
            // Verifica se o jogador tem propriedades típicas de conta original
            String displayName = player.getDisplayName();
            String playerListName = player.getPlayerListName();
            
            // Contas originais geralmente têm nomes consistentes
            boolean nameConsistency = displayName.equals(player.getName()) && 
                                    playerListName.equals(player.getName());
            
            // Verifica se o jogador tem permissões típicas de conta original
            boolean hasOriginalPermissions = player.hasPermission("minecraft.command.me") ||
                                           player.hasPermission("minecraft.command.say");
            
            // Verifica se o jogador tem skin (contas originais têm skins)
            boolean hasSkin = true; // Simplificado para compatibilidade
            
            return nameConsistency && hasOriginalPermissions && hasSkin;
        } catch (Exception e) {
            plugin.getLogger().warning("Erro na verificação de comportamento: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Método 5: Verificação por histórico de conexão
     */
    private boolean verifyByConnectionHistory(Player player) {
        try {
            // Verifica se o jogador já se conectou antes como conta original
            if (plugin.getDatabaseManager().isOriginalNameProtected(player.getName())) {
                UUID storedUUID = plugin.getDatabaseManager().getOriginalNameUUID(player.getName());
                return storedUUID != null && storedUUID.equals(player.getUniqueId());
            }
            
            // Se não tem histórico, assume que pode ser original
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("Erro na verificação de histórico: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Calcula pontuação de confiança baseada nos métodos de verificação
     */
    private int calculateConfidenceScore(boolean mojangAPI, boolean uuidVersion, 
                                       boolean uuidPattern, boolean clientBehavior, 
                                       boolean connectionHistory) {
        int score = 0;
        
        // Pesos para cada método
        if (mojangAPI) score += 40; // 40% - Mais importante
        if (uuidVersion) score += 20; // 20%
        if (uuidPattern) score += 15; // 15%
        if (clientBehavior) score += 15; // 15%
        if (connectionHistory) score += 10; // 10%
        
        return score;
    }
    
    /**
     * Classe para armazenar resultado da verificação
     */
    public static class VerificationResult {
        private final boolean isOriginal;
        private final String message;
        private final int confidenceScore;
        
        public VerificationResult(boolean isOriginal, String message, int confidenceScore) {
            this.isOriginal = isOriginal;
            this.message = message;
            this.confidenceScore = confidenceScore;
        }
        
        public boolean isOriginal() {
            return isOriginal;
        }
        
        public String getMessage() {
            return message;
        }
        
        public int getConfidenceScore() {
            return confidenceScore;
        }
    }
}