package com.eventbooking.event_booking_platform.config;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

public class RenderDatabaseEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String databaseUrl = environment.getProperty("DATABASE_URL");
        if (databaseUrl == null || databaseUrl.isBlank()) {
            return;
        }
        String raw = databaseUrl.trim();
        if (raw.startsWith("postgresql://")) {
            raw = "http://" + raw.substring("postgresql://".length());
        } else if (raw.startsWith("postgres://")) {
            raw = "http://" + raw.substring("postgres://".length());
        }
        java.net.URI uri = java.net.URI.create(raw);
        String userInfo = uri.getRawUserInfo();
        if (userInfo == null || uri.getHost() == null) {
            return;
        }
        int colon = userInfo.indexOf(':');
        String user = decode(colon < 0 ? userInfo : userInfo.substring(0, colon));
        String password = colon < 0 ? "" : decode(userInfo.substring(colon + 1));
        int port = uri.getPort() == -1 ? 5432 : uri.getPort();
        Map<String, Object> values = new HashMap<>();
        values.put("spring.datasource.url", "jdbc:postgresql://" + uri.getHost() + ":" + port + uri.getPath());
        values.put("spring.datasource.username", user);
        values.put("spring.datasource.password", password);
        values.put("spring.datasource.driver-class-name", "org.postgresql.Driver");
        environment.getPropertySources().addFirst(new MapPropertySource("render-database", values));
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
