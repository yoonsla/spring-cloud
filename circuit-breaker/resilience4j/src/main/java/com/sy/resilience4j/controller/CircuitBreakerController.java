package com.sy.resilience4j.controller;

import com.sy.resilience4j.service.CircuitBreakerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/cats")
public class CircuitBreakerController {

    private final CircuitBreakerService circuitBreakerService;

    @GetMapping("/{id}")
    public String getCatImage(@PathVariable Long id) {
        log.info(":::::::::::::::: Call :::::::::::::::");
        return circuitBreakerService.catImage(id);
    }
}
