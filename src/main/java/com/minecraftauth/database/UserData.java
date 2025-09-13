package com.minecraftauth.database;

import java.sql.Timestamp;
import java.util.UUID;

public class UserData {
    private final UUID uuid;
    private final String username;
    private final String passwordHash;
    private final boolean isOriginal;
    private final Timestamp registeredAt;
    private final Timestamp lastLogin;
    private final int loginAttempts;
    private final Timestamp lockedUntil;

    public UserData(UUID uuid, String username, String passwordHash, boolean isOriginal,
                   Timestamp registeredAt, Timestamp lastLogin, int loginAttempts, Timestamp lockedUntil) {
        this.uuid = uuid;
        this.username = username;
        this.passwordHash = passwordHash;
        this.isOriginal = isOriginal;
        this.registeredAt = registeredAt;
        this.lastLogin = lastLogin;
        this.loginAttempts = loginAttempts;
        this.lockedUntil = lockedUntil;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean isOriginal() {
        return isOriginal;
    }

    public Timestamp getRegisteredAt() {
        return registeredAt;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public int getLoginAttempts() {
        return loginAttempts;
    }

    public Timestamp getLockedUntil() {
        return lockedUntil;
    }

    public boolean isLocked() {
        return lockedUntil != null && lockedUntil.after(new Timestamp(System.currentTimeMillis()));
    }
}