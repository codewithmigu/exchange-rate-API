package com.exchange.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.management.timer.Timer;

@Component
@Slf4j
public class CacheScheduler {
    public static final String CACHE_NAME = "exchange-app-cache";

    @Scheduled(fixedRate = Timer.ONE_MINUTE)
    @CacheEvict(value = { CACHE_NAME })
    public void evictCache() {
        log.atDebug().log("Cache evicted");
    }
}
