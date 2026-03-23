package org.example.poc.service;

public interface JwtTokenBlacklistService {
    void blacklist(String token, long ttlSeconds);
    boolean isBlacklisted(String token);
}
