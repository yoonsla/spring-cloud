package com.sy.thirdservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/third-service")
@Slf4j
public class ThirdController {

    @GetMapping("/hello")
    public String hello(@RequestHeader(value = "third-header-request") String header) {
        log.info("Header value ===> {}", header);
        return "hello every third";
    }
}
