package com.minecraftauth.auth;

import com.minecraftauth.database.DatabaseManager;
import com.minecraftauth.database.UserData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AuthManager {
    private final DatabaseManager databaseManager;
    private final FileConfiguration config;
    private final Map<UUID, AuthSession> activeSessions = new HashMap<>();
    private final Map<UUID, Long> pendingRegistrations = new HashMap<>();

    public AuthManager(DatabaseManager databaseManager, FileConfiguration config) {
        this.databaseManager = databaseManager;
        this.config = config;
    }

    public CompletableFuture<AuthResult> authenticatePlayer(Player player, String password) {
        return CompletableFuture.supplyAsync(() -> {
            UUID uuid = player.getUniqueId();
            String username = player.getName();

            try {
                // Verificar se a conta é original
                boolean isOriginalAccount = databaseManager.isOriginalAccount(username).get();
                
                if (isOriginalAccount) {
                    // Conta original - login automático
                    return handleOriginalAccount(player);
                } else {
                    // Conta pirata - verificar registro/login
                    return handlePirateAccount(player, password);
                }
                
            } catch (Exception e) {
                Bukkit.getLogger().severe("[MinecraftAuth] Erro na autenticação: " + e.getMessage());
                return new AuthResult(false, "Erro interno do servidor. Tente novamente.");
            }
        });
    }

    private AuthResult handleOriginalAccount(Player player) {
        UUID uuid = player.getUniqueId();
        String username = player.getName();

        // Verificar se já existe no banco
        try {
            UserData userData = databaseManager.getUserData(uuid).get();
            
            if (userData == null) {
                // Primeira vez - criar conta original
                String defaultPassword = generateSecurePassword();
                String passwordHash = BCrypt.hashpw(defaultPassword, BCrypt.gensalt());
                
                boolean success = databaseManager.registerUser(uuid, username, passwordHash, true).get();
                if (!success) {
                    return new AuthResult(false, "Erro ao criar conta original.");
                }
                
                // Adicionar à lista de contas originais
                databaseManager.addOriginalAccount(username, uuid).get();
                
                // Criar sessão
                createSession(player);
                
                // Enviar senha por mensagem privada
                player.sendMessage("§a[MinecraftAuth] §eConta original detectada!");
                player.sendMessage("§a[MinecraftAuth] §eSua senha padrão é: §c" + defaultPassword);
                player.sendMessage("§a[MinecraftAuth] §eUse /changepassword para alterar sua senha.");
                
                return new AuthResult(true, "Login automático realizado com sucesso!");
            } else {
                // Conta já existe - login automático
                databaseManager.updateLastLogin(uuid).get();
                createSession(player);
                
                player.sendMessage("§a[MinecraftAuth] §eLogin automático realizado!");
                return new AuthResult(true, "Login realizado com sucesso!");
            }
            
        } catch (Exception e) {
            Bukkit.getLogger().severe("[MinecraftAuth] Erro ao processar conta original: " + e.getMessage());
            return new AuthResult(false, "Erro ao processar conta original.");
        }
    }

    private AuthResult handlePirateAccount(Player player, String password) {
        UUID uuid = player.getUniqueId();
        String username = player.getName();

        try {
            UserData userData = databaseManager.getUserData(uuid).get();
            
            if (userData == null) {
                return new AuthResult(false, "Conta não registrada. Use /register <senha> <confirmar_senha> para se registrar.");
            }

            // Verificar se a conta está bloqueada
            if (userData.isLocked()) {
                return new AuthResult(false, "Conta temporariamente bloqueada. Tente novamente mais tarde.");
            }

            // Verificar senha
            if (!BCrypt.checkpw(password, userData.getPasswordHash())) {
                // Incrementar tentativas de login
                databaseManager.incrementLoginAttempts(uuid).get();
                
                int maxAttempts = config.getInt("security.max_login_attempts", 5);
                if (userData.getLoginAttempts() + 1 >= maxAttempts) {
                    // Bloquear conta
                    int lockDuration = config.getInt("security.lock_duration_minutes", 30);
                    databaseManager.lockAccount(uuid, lockDuration).get();
                    return new AuthResult(false, "Muitas tentativas de login incorretas. Conta bloqueada por " + lockDuration + " minutos.");
                }
                
                return new AuthResult(false, "Senha incorreta. Tentativas restantes: " + (maxAttempts - userData.getLoginAttempts() - 1));
            }

            // Login bem-sucedido
            databaseManager.updateLastLogin(uuid).get();
            createSession(player);
            
            player.sendMessage("§a[MinecraftAuth] §eLogin realizado com sucesso!");
            return new AuthResult(true, "Login realizado com sucesso!");

        } catch (Exception e) {
            Bukkit.getLogger().severe("[MinecraftAuth] Erro ao processar conta pirata: " + e.getMessage());
            return new AuthResult(false, "Erro interno do servidor. Tente novamente.");
        }
    }

    public CompletableFuture<AuthResult> registerPlayer(Player player, String password, String confirmPassword) {
        return CompletableFuture.supplyAsync(() -> {
            UUID uuid = player.getUniqueId();
            String username = player.getName();

            try {
                // Verificar se já está registrado
                UserData existingUser = databaseManager.getUserData(uuid).get();
                if (existingUser != null) {
                    return new AuthResult(false, "Você já está registrado! Use /login <senha> para fazer login.");
                }

                // Verificar se o username já existe
                UserData existingUsername = databaseManager.getUserDataByUsername(username).get();
                if (existingUsername != null) {
                    return new AuthResult(false, "Este nome de usuário já está em uso!");
                }

                // Verificar se é uma conta original
                boolean isOriginalAccount = databaseManager.isOriginalAccount(username).get();
                if (isOriginalAccount) {
                    return new AuthResult(false, "Este é um nome de conta original! Contas originais fazem login automaticamente.");
                }

                // Validar senha
                if (!password.equals(confirmPassword)) {
                    return new AuthResult(false, "As senhas não coincidem!");
                }

                if (password.length() < config.getInt("security.min_password_length", 6)) {
                    return new AuthResult(false, "A senha deve ter pelo menos " + config.getInt("security.min_password_length", 6) + " caracteres!");
                }

                // Registrar usuário
                String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
                boolean success = databaseManager.registerUser(uuid, username, passwordHash, false).get();
                
                if (success) {
                    createSession(player);
                    player.sendMessage("§a[MinecraftAuth] §eRegistro realizado com sucesso!");
                    return new AuthResult(true, "Registro realizado com sucesso!");
                } else {
                    return new AuthResult(false, "Erro ao registrar conta. Tente novamente.");
                }

            } catch (Exception e) {
                Bukkit.getLogger().severe("[MinecraftAuth] Erro no registro: " + e.getMessage());
                return new AuthResult(false, "Erro interno do servidor. Tente novamente.");
            }
        });
    }

    public CompletableFuture<AuthResult> changePassword(Player player, String currentPassword, String newPassword, String confirmNewPassword) {
        return CompletableFuture.supplyAsync(() -> {
            UUID uuid = player.getUniqueId();

            try {
                UserData userData = databaseManager.getUserData(uuid).get();
                if (userData == null) {
                    return new AuthResult(false, "Conta não encontrada!");
                }

                // Verificar senha atual
                if (!BCrypt.checkpw(currentPassword, userData.getPasswordHash())) {
                    return new AuthResult(false, "Senha atual incorreta!");
                }

                // Validar nova senha
                if (!newPassword.equals(confirmNewPassword)) {
                    return new AuthResult(false, "As novas senhas não coincidem!");
                }

                if (newPassword.length() < config.getInt("security.min_password_length", 6)) {
                    return new AuthResult(false, "A nova senha deve ter pelo menos " + config.getInt("security.min_password_length", 6) + " caracteres!");
                }

                // Atualizar senha
                String newPasswordHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
                boolean success = databaseManager.updatePassword(uuid, newPasswordHash).get();
                
                if (success) {
                    player.sendMessage("§a[MinecraftAuth] §eSenha alterada com sucesso!");
                    return new AuthResult(true, "Senha alterada com sucesso!");
                } else {
                    return new AuthResult(false, "Erro ao alterar senha. Tente novamente.");
                }

            } catch (Exception e) {
                Bukkit.getLogger().severe("[MinecraftAuth] Erro ao alterar senha: " + e.getMessage());
                return new AuthResult(false, "Erro interno do servidor. Tente novamente.");
            }
        });
    }

    private void createSession(Player player) {
        UUID uuid = player.getUniqueId();
        long sessionDuration = config.getLong("security.session_duration_minutes", 60) * 60 * 1000; // Converter para millisegundos
        
        AuthSession session = new AuthSession(uuid, System.currentTimeMillis() + sessionDuration);
        activeSessions.put(uuid, session);
        
        // Agendar remoção da sessão
        new BukkitRunnable() {
            @Override
            public void run() {
                activeSessions.remove(uuid);
            }
        }.runTaskLater(Bukkit.getPluginManager().getPlugin("MinecraftAuth"), sessionDuration / 50); // Converter para ticks
    }

    public boolean isAuthenticated(Player player) {
        UUID uuid = player.getUniqueId();
        AuthSession session = activeSessions.get(uuid);
        
        if (session == null) {
            return false;
        }
        
        if (session.isExpired()) {
            activeSessions.remove(uuid);
            return false;
        }
        
        return true;
    }

    public void logout(Player player) {
        UUID uuid = player.getUniqueId();
        activeSessions.remove(uuid);
    }

    private String generateSecurePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        
        return password.toString();
    }

    public void cleanup() {
        activeSessions.clear();
    }
}