package com.sy.resilience4j.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CircuitBreakerService {

    @CircuitBreaker(name = "cat-image-circuit-breaker", fallbackMethod = "fallbackCatImage")
    public String catImage(Long id) {
        if (id < 10L) {
            return id + " cat's image.png";
        }
        throw new RuntimeException("there is no cat's image for " + id);
    }

    private String fallbackCatImage(Long id, Throwable throwable) {
        throwable.printStackTrace();
        return "fallback cat image.png";
    }
}
