package com.eventbooking.gateway;

interface TokenBucket extends AutoCloseable {

    boolean tryConsume(String key);

    @Override
    default void close() {
    }
}
