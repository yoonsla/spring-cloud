package com.sy.feignclient.config;

import static feign.Logger.Level.FULL;

import com.sy.feignclient.ClientApplication;
import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Retryer;
import java.util.concurrent.TimeUnit;
import org.bouncycastle.util.Arrays;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;

@Configuration
@EnableFeignClients(basePackageClasses = {ClientApplication.class})
public class OpenFeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            if (Arrays.isNullOrEmpty(requestTemplate.body()) && !isGetOrDelete(requestTemplate)) {
                requestTemplate.body("{}");
            }
        };
    }

    private boolean isGetOrDelete(RequestTemplate requestTemplate) {
        return Request.HttpMethod.GET.name().equals(requestTemplate.method())
            || Request.HttpMethod.DELETE.name().equals(requestTemplate.method());
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return FULL;
    }

    @Bean
    public FeignFormatterRegistrar dateTimeFormatterRegistrar() {
        return registry -> {
            DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
            registrar.setUseIsoFormat(true);
            registrar.registerFormatters(registry);
        };
    }

    @Bean
    Retryer.Default retryer() {
        // 0.1초의 간격으로 시작해 최대 3초의 간격으로 점점 증가하며, 최대5번 재시도한다.
        return new Retryer.Default(100L, TimeUnit.SECONDS.toMillis(3L), 5);
    }
}
