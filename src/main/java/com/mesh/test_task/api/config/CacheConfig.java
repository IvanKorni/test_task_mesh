package com.mesh.test_task.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheManagerProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class CacheConfig {
    public static final String USER_SEARCH_CACHE = "userSearch";

    @Bean
    public CacheManager cacheManager(@Value("${spring.cache.caffeine.spec}") String caffeineSpec) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(USER_SEARCH_CACHE);
        cacheManager.setCacheSpecification(caffeineSpec);
        return new TransactionAwareCacheManagerProxy(cacheManager);
    }
}
