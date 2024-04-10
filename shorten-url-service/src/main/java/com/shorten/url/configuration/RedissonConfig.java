package com.shorten.url.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("redisson")
@Data
public class RedissonConfig {
    private String redisUrl;
}
