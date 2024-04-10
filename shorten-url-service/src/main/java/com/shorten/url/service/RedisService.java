package com.shorten.url.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

    @Autowired
    private RedissonClient redissonClient;

    public void putKeyValue(String key, String value) {
        redissonClient.getBucket(key).set(value);
    }

    public String getValue(String key){
        RBucket<String> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }
}
