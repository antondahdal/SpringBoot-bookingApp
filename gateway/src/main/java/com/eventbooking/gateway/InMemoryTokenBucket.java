package com.eventbooking.gateway;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

final class InMemoryTokenBucket implements TokenBucket {

    private final int capacity;
    private final long refillMs;
    private final LongSupplier clock;
    private final ConcurrentHashMap<String, State> jars = new ConcurrentHashMap<>();

    InMemoryTokenBucket(int capacity, long refillMs) {
        this(capacity, refillMs, System::currentTimeMillis);
    }

    InMemoryTokenBucket(int capacity, long refillMs, LongSupplier clock) {
        this.capacity = capacity;
        this.refillMs = refillMs;
        this.clock = clock;
    }

    @Override
    public boolean tryConsume(String key) {
        State state = jars.computeIfAbsent(key, ignored -> new State(capacity, clock.getAsLong()));
        synchronized (state) {
            long now = clock.getAsLong();
            long elapsed = now - state.updatedAt;
            if (elapsed > 0 && refillMs > 0) {
                state.tokens = Math.min(capacity, state.tokens + (double) elapsed / refillMs);
                state.updatedAt = now;
            }
            if (state.tokens < 1) {
                return false;
            }
            state.tokens -= 1;
            return true;
        }
    }

    private static final class State {
        private double tokens;
        private long updatedAt;

        private State(double tokens, long updatedAt) {
            this.tokens = tokens;
            this.updatedAt = updatedAt;
        }
    }
}
