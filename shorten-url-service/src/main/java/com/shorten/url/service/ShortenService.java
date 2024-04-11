package com.shorten.url.service;

import com.shorten.url.constant.ShortenConstants;
import com.shorten.url.dto.ShortenRequest;
import com.shorten.url.entity.Shorten;
import com.shorten.url.repository.ShortenRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ShortenService {

    private final RedisService redisService;
    private final BloomFilterService bloomFilterService;
    private final ShortenRepository shortenRepository;

    private final static int generateRandomStringLength = 7;
    private final SecureRandom random = new SecureRandom();

    @SneakyThrows
    public String createShortenURL(ShortenRequest shortenRequest) {

        String hashShortenURL = generateRandomString(generateRandomStringLength);
        boolean shortenURLExist = bloomFilterService.checkShortenURLAvailability(hashShortenURL);

        while (shortenURLExist){
            hashShortenURL = generateRandomString(generateRandomStringLength);
            shortenURLExist = bloomFilterService.checkShortenURLAvailability(hashShortenURL);
        }

        redisService.putKeyValue(hashShortenURL, shortenRequest.getLongUrl());
        bloomFilterService.addData(hashShortenURL);
        shortenRepository.save(Shorten.builder().shortenUrl(hashShortenURL).longUrl(shortenRequest.getLongUrl()).build());

        return ShortenConstants.baseUrl.concat(hashShortenURL);

    }

    public String getLongURL(String shortenURL) {

        String longURL = redisService.getValue(shortenURL);
        if (StringUtils.isNotEmpty(longURL))
            return longURL;

        Optional<Shorten> longURLFromDB = shortenRepository.findById(shortenURL);
        if (longURLFromDB.isEmpty())
            throw new RuntimeException("LongURL not exist!");

        return longURLFromDB.get().getLongUrl();
    }

    public String generateRandomString(int length) {
        String characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }
}
