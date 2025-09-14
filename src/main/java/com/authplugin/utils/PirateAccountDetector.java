package com.authplugin.utils;

import com.authplugin.AuthPlugin;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PirateAccountDetector {
    
    private AuthPlugin plugin;
    private Map<UUID, PlayerBehavior> playerBehaviors;
    private Map<String, Integer> suspiciousNames;
    private Map<String, Long> connectionAttempts;
    
    public PirateAccountDetector(AuthPlugin plugin) {
        this.plugin = plugin;
        this.playerBehaviors = new ConcurrentHashMap<>();
        this.suspiciousNames = new ConcurrentHashMap<>();
        this.connectionAttempts = new ConcurrentHashMap<>();
    }
    
    /**
     * Analisa comportamento do jogador para detectar contas piratas
     */
    public boolean isPirateAccount(Player player) {
        String playerName = player.getName();
        UUID playerUUID = player.getUniqueId();
        
        plugin.getLogger().info("🔍 Analisando comportamento de: " + playerName);
        
        // Método 1: Verificação de padrões de nome suspeitos
        boolean suspiciousName = checkSuspiciousNamePatterns(playerName);
        plugin.getLogger().info("📝 Padrão de nome: " + (suspiciousName ? "❌ SUSPEITO" : "✅ NORMAL"));
        
        // Método 2: Verificação de tentativas de conexão
        boolean suspiciousConnections = checkSuspiciousConnections(playerName);
        plugin.getLogger().info("🔗 Tentativas de conexão: " + (suspiciousConnections ? "❌ SUSPEITO" : "✅ NORMAL"));
        
        // Método 3: Verificação de comportamento de jogo
        boolean suspiciousBehavior = checkSuspiciousBehavior(player);
        plugin.getLogger().info("🎮 Comportamento: " + (suspiciousBehavior ? "❌ SUSPEITO" : "✅ NORMAL"));
        
        // Método 4: Verificação de timing de conexão
        boolean suspiciousTiming = checkSuspiciousTiming(player);
        plugin.getLogger().info("⏰ Timing: " + (suspiciousTiming ? "❌ SUSPEITO" : "✅ NORMAL"));
        
        // Método 5: Verificação de recursos do cliente
        boolean suspiciousResources = checkSuspiciousResources(player);
        plugin.getLogger().info("💻 Recursos: " + (suspiciousResources ? "❌ SUSPEITO" : "✅ NORMAL"));
        
        // Calcula pontuação de suspeita
        int suspicionScore = calculateSuspicionScore(
            suspiciousName, suspiciousConnections, suspiciousBehavior, 
            suspiciousTiming, suspiciousResources
        );
        
        plugin.getLogger().info("🎯 Pontuação de suspeita: " + suspicionScore + "/100");
        
        // Se pontuação for alta, é provável conta pirata
        boolean isPirate = suspicionScore >= 60;
        
        if (isPirate) {
            plugin.getLogger().info("🚨 CONTA PIRATA DETECTADA: " + playerName);
            // Registra comportamento suspeito
            recordSuspiciousBehavior(player, suspicionScore);
        } else {
            plugin.getLogger().info("✅ Conta aparenta ser legítima: " + playerName);
        }
        
        return isPirate;
    }
    
    /**
     * Verifica padrões de nome suspeitos
     */
    private boolean checkSuspiciousNamePatterns(String playerName) {
        // Nomes muito curtos ou muito longos
        if (playerName.length() < 3 || playerName.length() > 16) {
            return true;
        }
        
        // Nomes com muitos números
        long digitCount = playerName.chars().filter(Character::isDigit).count();
        if (digitCount > playerName.length() / 2) {
            return true;
        }
        
        // Nomes com caracteres especiais suspeitos
        if (playerName.matches(".*[^a-zA-Z0-9_].*")) {
            return true;
        }
        
        // Nomes que são variações de nomes famosos
        String[] famousNames = {"Notch", "Dinnerbone", "jeb_", "Grumm", "Dinnerbone", "Herobrine"};
        for (String famous : famousNames) {
            if (playerName.toLowerCase().contains(famous.toLowerCase()) && 
                !playerName.equalsIgnoreCase(famous)) {
                return true;
            }
        }
        
        // Nomes com padrões repetitivos
        if (playerName.matches(".*(.)\\1{2,}.*")) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Verifica tentativas de conexão suspeitas
     */
    private boolean checkSuspiciousConnections(String playerName) {
        long currentTime = System.currentTimeMillis();
        String key = playerName.toLowerCase();
        
        // Verifica se houve muitas tentativas recentes
        if (connectionAttempts.containsKey(key)) {
            long lastAttempt = connectionAttempts.get(key);
            if (currentTime - lastAttempt < 5000) { // Menos de 5 segundos
                return true;
            }
        }
        
        // Registra tentativa atual
        connectionAttempts.put(key, currentTime);
        
        // Verifica se o nome está na lista de suspeitos
        if (suspiciousNames.containsKey(key)) {
            int attempts = suspiciousNames.get(key);
            suspiciousNames.put(key, attempts + 1);
            return attempts > 3; // Mais de 3 tentativas suspeitas
        } else {
            suspiciousNames.put(key, 1);
        }
        
        return false;
    }
    
    /**
     * Verifica comportamento suspeito do jogador
     */
    private boolean checkSuspiciousBehavior(Player player) {
        PlayerBehavior behavior = playerBehaviors.get(player.getUniqueId());
        if (behavior == null) {
            behavior = new PlayerBehavior();
            playerBehaviors.put(player.getUniqueId(), behavior);
        }
        
        // Verifica se o jogador está se movendo muito rapidamente
        if (behavior.getMovementSpeed() > 0.5) {
            return true;
        }
        
        // Verifica se o jogador está fazendo muitas ações por segundo
        if (behavior.getActionsPerSecond() > 10) {
            return true;
        }
        
        // Verifica se o jogador está tentando usar comandos restritos
        if (behavior.getRestrictedCommandAttempts() > 5) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Verifica timing suspeito de conexão
     */
    private boolean checkSuspiciousTiming(Player player) {
        long currentTime = System.currentTimeMillis();
        
        // Verifica se o jogador está se conectando em horários suspeitos
        // (implementação básica - pode ser expandida)
        
        // Verifica se há muitos jogadores se conectando ao mesmo tempo
        int onlineCount = plugin.getServer().getOnlinePlayers().size();
        if (onlineCount > 50) { // Muitos jogadores online
            return true;
        }
        
        return false;
    }
    
    /**
     * Verifica recursos suspeitos do cliente
     */
    private boolean checkSuspiciousResources(Player player) {
        try {
            // Verificação simplificada para compatibilidade
            // Contas originais geralmente têm recursos mais completos
            return false; // Simplificado para compatibilidade
        } catch (Exception e) {
            plugin.getLogger().warning("Erro na verificação de recursos: " + e.getMessage());
            return true; // Se não conseguir verificar, assume suspeito
        }
    }
    
    /**
     * Calcula pontuação de suspeita
     */
    private int calculateSuspicionScore(boolean suspiciousName, boolean suspiciousConnections, 
                                      boolean suspiciousBehavior, boolean suspiciousTiming, 
                                      boolean suspiciousResources) {
        int score = 0;
        
        if (suspiciousName) score += 25;
        if (suspiciousConnections) score += 30;
        if (suspiciousBehavior) score += 20;
        if (suspiciousTiming) score += 15;
        if (suspiciousResources) score += 10;
        
        return score;
    }
    
    /**
     * Registra comportamento suspeito
     */
    private void recordSuspiciousBehavior(Player player, int suspicionScore) {
        String playerName = player.getName();
        plugin.getLogger().warning("🚨 Comportamento suspeito registrado para " + playerName + 
                                 " (Pontuação: " + suspicionScore + ")");
        
        // Pode implementar banimento automático ou outras ações
        if (suspicionScore >= 80) {
            plugin.getLogger().severe("🚨 CONTA PIRATA ALTAMENTE SUSPEITA: " + playerName);
            // Aqui pode implementar banimento automático
        }
    }
    
    /**
     * Classe para rastrear comportamento do jogador
     */
    private static class PlayerBehavior {
        private double movementSpeed = 0.0;
        private int actionsPerSecond = 0;
        private int restrictedCommandAttempts = 0;
        private long lastUpdate = System.currentTimeMillis();
        
        public double getMovementSpeed() {
            return movementSpeed;
        }
        
        public void setMovementSpeed(double movementSpeed) {
            this.movementSpeed = movementSpeed;
        }
        
        public int getActionsPerSecond() {
            return actionsPerSecond;
        }
        
        public void incrementActions() {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdate < 1000) {
                actionsPerSecond++;
            } else {
                actionsPerSecond = 1;
                lastUpdate = currentTime;
            }
        }
        
        public int getRestrictedCommandAttempts() {
            return restrictedCommandAttempts;
        }
        
        public void incrementRestrictedCommandAttempts() {
            this.restrictedCommandAttempts++;
        }
    }
}