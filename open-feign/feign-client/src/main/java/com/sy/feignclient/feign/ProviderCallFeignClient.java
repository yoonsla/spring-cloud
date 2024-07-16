package com.sy.feignclient.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "provider", url = "http://localhost:8777")
public interface ProviderCallFeignClient {

    @GetMapping("/provider")
    String callProvider();
}
