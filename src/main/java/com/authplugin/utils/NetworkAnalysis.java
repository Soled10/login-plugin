package com.authplugin.utils;

import com.authplugin.AuthPlugin;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;

public class NetworkAnalysis {
    
    private AuthPlugin plugin;
    private Map<String, NetworkInfo> networkCache;
    private Map<String, Long> ipConnectionCount;
    private Map<String, Long> lastConnectionTime;
    
    public NetworkAnalysis(AuthPlugin plugin) {
        this.plugin = plugin;
        this.networkCache = new ConcurrentHashMap<>();
        this.ipConnectionCount = new ConcurrentHashMap<>();
        this.lastConnectionTime = new ConcurrentHashMap<>();
    }
    
    /**
     * Analisa a rede do jogador para detectar contas piratas
     */
    public CompletableFuture<NetworkAnalysisResult> analyzeNetwork(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String playerName = player.getName();
                InetAddress address = player.getAddress().getAddress();
                String ip = address.getHostAddress();
                
                plugin.getLogger().info("🌐 Analisando rede de: " + playerName + " (" + ip + ")");
                
                // Método 1: Verificação de IP suspeito
                boolean suspiciousIP = checkSuspiciousIP(ip);
                plugin.getLogger().info("🔍 IP suspeito: " + (suspiciousIP ? "❌ SIM" : "✅ NÃO"));
                
                // Método 2: Verificação de geolocalização
                boolean suspiciousLocation = checkSuspiciousLocation(ip);
                plugin.getLogger().info("🌍 Localização suspeita: " + (suspiciousLocation ? "❌ SIM" : "✅ NÃO"));
                
                // Método 3: Verificação de provedor de internet
                boolean suspiciousISP = checkSuspiciousISP(ip);
                plugin.getLogger().info("🏢 ISP suspeito: " + (suspiciousISP ? "❌ SIM" : "✅ NÃO"));
                
                // Método 4: Verificação de múltiplas conexões
                boolean multipleConnections = checkMultipleConnections(ip);
                plugin.getLogger().info("👥 Múltiplas conexões: " + (multipleConnections ? "❌ SIM" : "✅ NÃO"));
                
                // Método 5: Verificação de velocidade de conexão
                boolean suspiciousSpeed = checkSuspiciousSpeed(player);
                plugin.getLogger().info("⚡ Velocidade suspeita: " + (suspiciousSpeed ? "❌ SIM" : "✅ NÃO"));
                
                // Calcula pontuação de suspeita
                int suspicionScore = calculateNetworkSuspicionScore(
                    suspiciousIP, suspiciousLocation, suspiciousISP, 
                    multipleConnections, suspiciousSpeed
                );
                
                plugin.getLogger().info("🎯 Pontuação de suspeita de rede: " + suspicionScore + "/100");
                
                // Determina se é suspeito
                boolean isSuspicious = suspicionScore >= 60;
                
                return new NetworkAnalysisResult(
                    ip, 
                    isSuspicious, 
                    suspicionScore, 
                    getNetworkInfo(ip)
                );
                
            } catch (Exception e) {
                plugin.getLogger().warning("Erro na análise de rede: " + e.getMessage());
                return new NetworkAnalysisResult(
                    "unknown", 
                    true, 
                    100, 
                    "Erro na análise"
                );
            }
        });
    }
    
    /**
     * Verifica se o IP é suspeito
     */
    private boolean checkSuspiciousIP(String ip) {
        // Verifica se é IP local (pode ser teste)
        if (ip.startsWith("127.0.0.1") || ip.startsWith("192.168.") || ip.startsWith("10.")) {
            return false; // IPs locais são normais para testes
        }
        
        // Verifica se é IP de VPN conhecido (implementação básica)
        String[] vpnRanges = {
            "185.107.56.", "185.107.57.", "185.107.58.", "185.107.59.",
            "45.8.104.", "45.8.105.", "45.8.106.", "45.8.107.",
            "103.149.162.", "103.149.163.", "103.149.164.", "103.149.165."
        };
        
        for (String range : vpnRanges) {
            if (ip.startsWith(range)) {
                return true;
            }
        }
        
        // Verifica se é IP de proxy conhecido
        String[] proxyRanges = {
            "5.188.62.", "5.188.63.", "5.188.64.", "5.188.65.",
            "185.220.100.", "185.220.101.", "185.220.102.", "185.220.103."
        };
        
        for (String range : proxyRanges) {
            if (ip.startsWith(range)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Verifica se a localização é suspeita
     */
    private boolean checkSuspiciousLocation(String ip) {
        try {
            // Usa API de geolocalização (exemplo com ipapi.co)
            URL url = new URL("http://ipapi.co/" + ip + "/json/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            String jsonResponse = response.toString();
            
            // Verifica se é de país suspeito (exemplo)
            String[] suspiciousCountries = {"CN", "RU", "KP", "IR"};
            for (String country : suspiciousCountries) {
                if (jsonResponse.contains("\"country_code\":\"" + country + "\"")) {
                    return true;
                }
            }
            
            return false;
        } catch (Exception e) {
            plugin.getLogger().warning("Erro na verificação de localização: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifica se o ISP é suspeito
     */
    private boolean checkSuspiciousISP(String ip) {
        try {
            // Usa API de informações de IP
            URL url = new URL("http://ipapi.co/" + ip + "/json/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            String jsonResponse = response.toString();
            
            // Verifica se é ISP suspeito (exemplo)
            String[] suspiciousISPs = {"VPN", "Proxy", "Tor", "Anonymous"};
            for (String isp : suspiciousISPs) {
                if (jsonResponse.toLowerCase().contains(isp.toLowerCase())) {
                    return true;
                }
            }
            
            return false;
        } catch (Exception e) {
            plugin.getLogger().warning("Erro na verificação de ISP: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifica se há múltiplas conexões do mesmo IP
     */
    private boolean checkMultipleConnections(String ip) {
        long currentTime = System.currentTimeMillis();
        
        // Verifica se há muitas conexões recentes do mesmo IP
        if (ipConnectionCount.containsKey(ip)) {
            long lastConnection = lastConnectionTime.getOrDefault(ip, 0L);
            long connectionCount = ipConnectionCount.get(ip);
            
            if (currentTime - lastConnection < 300000) { // 5 minutos
                ipConnectionCount.put(ip, connectionCount + 1);
                return connectionCount > 3; // Mais de 3 conexões em 5 minutos
            } else {
                ipConnectionCount.put(ip, 1L);
            }
        } else {
            ipConnectionCount.put(ip, 1L);
        }
        
        lastConnectionTime.put(ip, currentTime);
        return false;
    }
    
    /**
     * Verifica se a velocidade de conexão é suspeita
     */
    private boolean checkSuspiciousSpeed(Player player) {
        // Implementação básica - pode ser expandida
        // Verifica se o jogador está se conectando muito rapidamente
        
        long currentTime = System.currentTimeMillis();
        String playerName = player.getName();
        
        if (lastConnectionTime.containsKey(playerName)) {
            long lastConnection = lastConnectionTime.get(playerName);
            if (currentTime - lastConnection < 1000) { // Menos de 1 segundo
                return true;
            }
        }
        
        lastConnectionTime.put(playerName, currentTime);
        return false;
    }
    
    /**
     * Calcula pontuação de suspeita de rede
     */
    private int calculateNetworkSuspicionScore(boolean suspiciousIP, boolean suspiciousLocation, 
                                             boolean suspiciousISP, boolean multipleConnections, 
                                             boolean suspiciousSpeed) {
        int score = 0;
        
        if (suspiciousIP) score += 30;
        if (suspiciousLocation) score += 25;
        if (suspiciousISP) score += 25;
        if (multipleConnections) score += 15;
        if (suspiciousSpeed) score += 5;
        
        return score;
    }
    
    /**
     * Obtém informações da rede
     */
    private String getNetworkInfo(String ip) {
        if (networkCache.containsKey(ip)) {
            return networkCache.get(ip).toString();
        }
        
        try {
            URL url = new URL("http://ipapi.co/" + ip + "/json/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            String jsonResponse = response.toString();
            NetworkInfo info = new NetworkInfo(jsonResponse);
            networkCache.put(ip, info);
            
            return info.toString();
        } catch (Exception e) {
            plugin.getLogger().warning("Erro ao obter informações da rede: " + e.getMessage());
            return "Informações não disponíveis";
        }
    }
    
    /**
     * Classe para armazenar informações da rede
     */
    private static class NetworkInfo {
        private String country;
        private String region;
        private String city;
        private String isp;
        private String org;
        
        public NetworkInfo(String jsonResponse) {
            // Parse básico do JSON (implementação simplificada)
            this.country = extractValue(jsonResponse, "country_name");
            this.region = extractValue(jsonResponse, "region");
            this.city = extractValue(jsonResponse, "city");
            this.isp = extractValue(jsonResponse, "org");
            this.org = extractValue(jsonResponse, "org");
        }
        
        private String extractValue(String json, String key) {
            try {
                int startIndex = json.indexOf("\"" + key + "\":\"") + key.length() + 4;
                int endIndex = json.indexOf("\"", startIndex);
                if (startIndex > 3 && endIndex > startIndex) {
                    return json.substring(startIndex, endIndex);
                }
            } catch (Exception e) {
                // Ignore parsing errors
            }
            return "Unknown";
        }
        
        @Override
        public String toString() {
            return String.format("País: %s, Região: %s, Cidade: %s, ISP: %s", 
                               country, region, city, isp);
        }
    }
    
    /**
     * Classe para resultado da análise de rede
     */
    public static class NetworkAnalysisResult {
        private final String ip;
        private final boolean isSuspicious;
        private final int suspicionScore;
        private final String networkInfo;
        
        public NetworkAnalysisResult(String ip, boolean isSuspicious, int suspicionScore, String networkInfo) {
            this.ip = ip;
            this.isSuspicious = isSuspicious;
            this.suspicionScore = suspicionScore;
            this.networkInfo = networkInfo;
        }
        
        public String getIp() {
            return ip;
        }
        
        public boolean isSuspicious() {
            return isSuspicious;
        }
        
        public int getSuspicionScore() {
            return suspicionScore;
        }
        
        public String getNetworkInfo() {
            return networkInfo;
        }
    }
}