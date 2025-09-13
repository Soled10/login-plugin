package com.minecraftauth.auth;

import java.util.UUID;

public class AuthSession {
    private final UUID uuid;
    private final long expiresAt;

    public AuthSession(UUID uuid, long expiresAt) {
        this.uuid = uuid;
        this.expiresAt = expiresAt;
    }

    public UUID getUuid() {
        return uuid;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
}