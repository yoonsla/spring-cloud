package com.sy.resilience4j.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CircuitBreakerInitializer implements CommandLineRunner {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Override
    public void run(String... args) throws Exception {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("cat-image-circuit-breaker");
        circuitBreaker.reset();
    }
}
