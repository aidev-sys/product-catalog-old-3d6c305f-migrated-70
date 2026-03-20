package com.example.productcatalog.controller;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/cache/stats")
public class CacheStatsController {

    private final RedisCacheManager cacheManager;
    private final RabbitTemplate rabbitTemplate;

    public CacheStatsController(RedisCacheManager cacheManager, RabbitTemplate rabbitTemplate) {
        this.cacheManager = cacheManager;
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping
    public Map<String, Object> stats() {
        // Dummy implementation since Redis doesn't expose cache stats directly like Caffeine
        return Map.of(
            "size", "N/A",
            "hits", "N/A",
            "misses", "N/A",
            "hitRate", "N/A",
            "evictions", "N/A"
        );
    }

    @RabbitListener(queuesToDeclare = @org.springframework.amqp.rabbit.annotation.Queue(name = "cache.stats.request", durable = "true"))
    public void handleCacheStatsRequest(String request) {
        // Process the request and send response back
        String response = "Cache stats processed";
        rabbitTemplate.convertAndSend("cache.stats.response", response);
    }
}