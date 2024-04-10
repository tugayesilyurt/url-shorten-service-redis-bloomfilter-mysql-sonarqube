package com.shorten.url.service;

import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BloomFilterService {

    private final RedissonClient redissonClient;
    private RBloomFilter<String> shortenBloomFilter;

    @Synchronized
    public RBloomFilter<String> getShortenBloomFilter() {

        if (Objects.isNull(shortenBloomFilter)) {
            RBloomFilter<String> stringRBloomFilter = redissonClient
                    .getBloomFilter("shortenurl-bloomfilter");
            stringRBloomFilter.tryInit(99999, 0.001);
            shortenBloomFilter = stringRBloomFilter;
        }

        return shortenBloomFilter;
    }

    public boolean checkShortenURLAvailability(String shortenURL) {
        return getShortenBloomFilter().contains(shortenURL);
    }

    public void addData(String data){
        getShortenBloomFilter().add(data);
    }
}
