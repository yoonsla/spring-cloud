package com.sy.feignclient.controller;

import com.sy.feignclient.feign.ProviderCallFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/client")
public class ClientController {

    private final ProviderCallFeignClient providerCallFeignClient;

    @GetMapping
    public ResponseEntity client() {
        log.info(":::::::: call : {}", providerCallFeignClient.callProvider());
        return ResponseEntity.ok().body(providerCallFeignClient.callProvider());
    }
}
