package org.example.poc.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.poc.service.JwtTokenBlacklistService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisJwtTokenBlacklistService implements JwtTokenBlacklistService {
    private final StringRedisTemplate stringRedisTemplate;

    @Value("${security.jwt.blacklist.prefix:blacklist:token:}")
    private String keyPrefix;

    @Override
    public void blacklist(String token, long ttlSeconds) {
        if (token == null || token.isBlank() || ttlSeconds <= 0) {
            return;
        }

        try {
            stringRedisTemplate.opsForValue().set(buildKey(token), "1", Duration.ofSeconds(ttlSeconds));
        } catch (DataAccessException ex) {
            throw new IllegalStateException("Redis is unavailable for JWT blacklist", ex);
        }
    }

    @Override
    public boolean isBlacklisted(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        try {
            Boolean exists = stringRedisTemplate.hasKey(buildKey(token));
            return Boolean.TRUE.equals(exists);
        } catch (DataAccessException ex) {
            return false;
        }
    }

    private String buildKey(String token) {
        return keyPrefix + token;
    }
}
