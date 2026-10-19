package com.eventbooking.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RateLimitConfig {

    @Bean(destroyMethod = "close")
    TokenBucket tokenBucket(
            @Value("${app.ratelimit.capacity:5}") int capacity,
            @Value("${app.ratelimit.refill-ms:15000}") long refillMs,
            @Value("${app.ratelimit.redis-host:}") String redisHost,
            @Value("${app.ratelimit.redis-port:6379}") int redisPort,
            @Value("${REDIS_URL:}") String redisUrl) {
        if (redisUrl != null && !redisUrl.isBlank()) {
            return new RedisTokenBucket(redisUrl, capacity, refillMs);
        }
        if (redisHost == null || redisHost.isBlank()) {
            return new InMemoryTokenBucket(capacity, refillMs);
        }
        return new RedisTokenBucket(redisHost, redisPort, capacity, refillMs);
    }
}
