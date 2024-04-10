package com.shorten.url.configuration;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    @Autowired
    private RedissonConfig redissonConfig;

    @Bean
    public Config getConfig() {
        Config config = new Config();
        config.useSingleServer().setAddress(redissonConfig.getRedisUrl());
        return config;
    }

    @Bean
    public RedissonClient getRedisClient(Config config) {
        return Redisson.create(config);
    }
}