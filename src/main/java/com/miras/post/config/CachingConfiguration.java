package com.miras.post.config;

import com.miras.post.service.impl.PostServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

@Configuration
public class CachingConfiguration implements CachingConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(CachingConfiguration.class);

    @Override
    public CacheErrorHandler errorHandler() {
        return new RedisCacheErrorHandler();
    }

    public static class RedisCacheErrorHandler implements CacheErrorHandler {

        @Override
        public void handleCacheGetError(RuntimeException exception, Cache cache, @NonNull Object key) {
            logger.warn("Unable to get from cache {} : {}", cache.getName(), exception.getMessage());
        }

        @Override
        public void handleCachePutError(RuntimeException exception, Cache cache, @NonNull Object key, Object value) {
            logger.warn("Unable to put into cache {} : {}", cache.getName(), exception.getMessage());
        }

        @Override
        public void handleCacheEvictError(RuntimeException exception, Cache cache, @NonNull Object key) {
            logger.warn("Unable to evict from cache {} : {}", cache.getName(), exception.getMessage());
        }

        @Override
        public void handleCacheClearError(RuntimeException exception, Cache cache) {
            logger.warn("Unable to clean cache {} : {}", cache.getName(), exception.getMessage());
        }
    }
}
