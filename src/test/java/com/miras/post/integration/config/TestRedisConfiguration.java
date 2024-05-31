package com.miras.post.integration.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import static redis.embedded.RedisServer.newRedisServer;
import static redis.embedded.core.ExecutableProvider.REDIS_7_2_MACOSX_14_SONOMA_HANKCP;
import static redis.embedded.core.ExecutableProvider.newCachedUrlProvider;

@TestConfiguration
public class TestRedisConfiguration {

    private RedisServer redisServer;

    public TestRedisConfiguration() throws IOException {
        final Path cacheLocation = Paths.get(System.getProperty("java.io.tmpdir"), "redis-binary");
        redisServer = newRedisServer()
                .executableProvider(newCachedUrlProvider(cacheLocation, REDIS_7_2_MACOSX_14_SONOMA_HANKCP))
                .build();
    }

    @PostConstruct
    public void postConstruct() throws IOException {
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() throws IOException {
        redisServer.stop();
    }
}
