package com.team9.statistic_service.api.healty;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/actuator/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "statistic-service");

        return ResponseEntity.ok(health);
    }

    @GetMapping("/health")
    public ResponseEntity<String> simpleHealth() {
        return ResponseEntity.ok("Statistic Service is running!");
    }
}
