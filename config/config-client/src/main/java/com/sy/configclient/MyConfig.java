package com.sy.configclient;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@Setter
@Getter
@ConfigurationProperties("com.sy")
@RefreshScope
@ToString
public class MyConfig {

    private String profile;
    private String region;
}
