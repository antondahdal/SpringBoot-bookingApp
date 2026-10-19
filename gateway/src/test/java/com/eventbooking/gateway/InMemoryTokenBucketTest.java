package com.eventbooking.gateway;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;

class InMemoryTokenBucketTest {

    @Test
    void burstThenDrip() {
        AtomicLong now = new AtomicLong(0);
        InMemoryTokenBucket bucket = new InMemoryTokenBucket(2, 1_000, now::get);

        assertTrue(bucket.tryConsume("user:1"));
        assertTrue(bucket.tryConsume("user:1"));
        assertFalse(bucket.tryConsume("user:1"));

        now.set(1_000);
        assertTrue(bucket.tryConsume("user:1"));
        assertFalse(bucket.tryConsume("user:1"));
    }
}
