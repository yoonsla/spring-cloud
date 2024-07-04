package com.sy.configclient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/config")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ConfigController {

    private final MyConfig myConfig;

    @GetMapping
    public ResponseEntity<String> config() {
        log.info("my config ====> {}", myConfig);
        return ResponseEntity.ok(myConfig.toString());
    }
}
