package com.eventbooking.gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class GatewayHealthController {

    @GetMapping("/health")
    String health() {
        return "ok";
    }
}
