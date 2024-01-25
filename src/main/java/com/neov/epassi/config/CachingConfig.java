package com.neov.epassi.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CachingConfig {
  @Bean
  public ConcurrentMapCacheManager concurrentMapCacheManager() {
    return new ConcurrentMapCacheManager();
  }
}
