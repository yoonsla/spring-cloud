package com.sy.configclient.watcher;

import static org.springframework.util.StringUtils.hasText;

import jakarta.annotation.PostConstruct;
import java.io.Closeable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
public class CustomConfigClientWatch implements Closeable, EnvironmentAware {

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicReference<String> version = new AtomicReference<>();
    private final ConfigServicePropertySourceLocator locator;
    private final ContextRefresher refresher;

    private Environment environment;

    public CustomConfigClientWatch(ContextRefresher refresher, ConfigServicePropertySourceLocator locator) {
        this.refresher = refresher;
        this.locator = locator;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void start() {
        this.running.compareAndSet(false, true);
    }

    // initialDelayString : 애플리케이션 구동 후 Watcher 실행을 지연시킬 시간 (ms)
    // fixedDelayString : 설정 서버로부터 변경 여부를 확인할 시간 (ms)
    @Scheduled(
        initialDelayString = "${spring.cloud.config.watch.git.initialDelay:10000}",
        fixedDelayString = "${spring.cloud.config.watch.git.delay:5000}"
    )
    public void watchConfigServer() {
        if (this.running.get()) {
            String newVersion = fetchNewVersion();
            if (newVersion == null) {
                return;
            }
            String oldVersion = this.version.get();
            if (versionChanged(oldVersion, newVersion)) {
                this.version.set(newVersion);
                this.refresher.refresh();
            }
        }
    }

    private String fetchNewVersion() {
        try {
            CompositePropertySource propertySource = (CompositePropertySource) this.locator.locate(this.environment);
            return (String) propertySource.getProperty("config.client.version");
        } catch (NullPointerException e) {
            log.error("Cannot fetch from Cloud Config Server: ");
        }
        return null;
    }

    private static boolean versionChanged(String oldVersion, String newVersion) {
        return (!hasText(oldVersion) && hasText(newVersion)) || (hasText(oldVersion) && !oldVersion.equals(newVersion));
    }

    @Override
    public void close() {
        this.running.compareAndSet(true, false);
    }
}
