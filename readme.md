# Project

URL Shortening Services: Redis Integration with Bloom Filters in Spring Boot, MySQL, and SonarQube Analysis

## System Diagram

<img src="https://github.com/tugayesilyurt/url-shorten-service-redis-bloomfilter-mysql-sonarqube/blob/main/assets/systemdesign.png" width=70% height=70%>

## Tech Stack

- Java 21
- Spring Boot 3
- Redis 
- Redisson Bloom Filter
- MySQL
- PostgreSQL
- SonarQube
- Docker

## Installation

 - follow the steps:

```shell
   docker-compose up -d
   cd shorten-url-service
   mvn clean install
   run java application
```

## Using Bloom Filter

```java
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
