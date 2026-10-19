package com.eventbooking.gateway;

import io.lettuce.core.RedisClient;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

final class RedisTokenBucket implements TokenBucket {

    private static final String SCRIPT = """
            local key = KEYS[1]
            local capacity = tonumber(ARGV[1])
            local refillMs = tonumber(ARGV[2])
            local now = tonumber(ARGV[3])
            local data = redis.call('HMGET', key, 'tokens', 'ts')
            local tokens = tonumber(data[1])
            local ts = tonumber(data[2])
            if tokens == nil then
              tokens = capacity
              ts = now
            end
            if now > ts and refillMs > 0 then
              tokens = math.min(capacity, tokens + (now - ts) / refillMs)
            end
            local allowed = 0
            if tokens >= 1 then
              tokens = tokens - 1
              allowed = 1
            end
            redis.call('HSET', key, 'tokens', tostring(tokens), 'ts', tostring(now))
            redis.call('PEXPIRE', key, math.max(refillMs * capacity * 2, 60000))
            return allowed
            """;

    private final int capacity;
    private final long refillMs;
    private final RedisClient client;
    private final StatefulRedisConnection<String, String> connection;

    RedisTokenBucket(String host, int port, int capacity, long refillMs) {
        this(capacity, refillMs, "redis://" + host + ":" + port);
    }

    RedisTokenBucket(String redisUri, int capacity, long refillMs) {
        this(capacity, refillMs, redisUri);
    }

    private RedisTokenBucket(int capacity, long refillMs, String redisUri) {
        this.capacity = capacity;
        this.refillMs = refillMs;
        this.client = RedisClient.create(redisUri);
        this.connection = client.connect();
    }

    @Override
    public boolean tryConsume(String key) {
        RedisCommands<String, String> commands = connection.sync();
        Long allowed = commands.eval(
                SCRIPT,
                ScriptOutputType.INTEGER,
                new String[] { "rl:" + key },
                Integer.toString(capacity),
                Long.toString(refillMs),
                Long.toString(System.currentTimeMillis()));
        return allowed != null && allowed == 1L;
    }

    @Override
    public void close() {
        connection.close();
        client.shutdown();
    }
}
