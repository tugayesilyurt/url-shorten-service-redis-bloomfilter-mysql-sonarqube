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

## Initializing a Redisson Bloom Filter with `tryInit()`

When working with Redisson's Bloom Filter, you can use the `tryInit()` method to initialize the Bloom Filter with specific parameters. This method helps optimize the Bloom Filter's performance and memory usage based on your expected workload and desired false positive probability.

### Method Signature

- expectedInsertions (type: long): Represents the expected number of insertions into the Bloom Filter. Adjusting this parameter allows you to allocate memory efficiently based on your anticipated workload.

- falseProbability (type: double): Represents the desired false positive probability. Bloom Filters have a trade-off between memory usage and false positive rate. By specifying falseProbability, you indicate the acceptable rate at which false positives occur.

## Using Bloom Filter

```java
  stringRBloomFilter.tryInit(expectedInsertions, falseProbability);

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

## Spring Boot Test Container

    static final MySQLContainer MY_SQL_CONTAINER;

    static {
        MY_SQL_CONTAINER = new MySQLContainer("mysql:latest")
                .withUsername("shorten")
                .withPassword("123456")
                .withDatabaseName("shorten");

        MY_SQL_CONTAINER.start();

        GenericContainer redis = new GenericContainer("redis:3-alpine")
                .withExposedPorts(6379);
        redis.start();

        System.setProperty("redisson.redisUrl", "redis://" + redis.getContainerIpAddress()
                + ":" + redis.getFirstMappedPort());

    }

    @DynamicPropertySource
    public static void dynamicPropertySource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MY_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MY_SQL_CONTAINER::getPassword);
    }

## Spring Boot Test Controller

    @Test
    @DisplayName("integration test - create shorten url")
    public void givenLongURL_whenCreateShortenURL_thenReturnShortenURL() throws Exception {

        // given - precondition or setup
        ShortenRequest request = ShortenRequest.builder()
                .longUrl("https://medium.com").build();

        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(post("/api/v1/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then - verify the result or output using assert statements
        response.andDo(print()).
                andExpect(status().isCreated());

    }

    @Test
    @DisplayName("integration test - get long url")
    public void givenShortenURL_whenGetLongURL_thenReturnLongURL() throws Exception {

        // given - precondition or setup
        String shortenURL = "9464b52";

        Shorten willSaveShorten = Shorten.builder()
                .shortenUrl(shortenURL)
                .longUrl("https://medium.com").build();

        shortenRepository.save(willSaveShorten);

        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/api/v1/shorten")
                .param("shortUrl", shortenURL)
                .contentType(MediaType.APPLICATION_JSON));

        // then - verify the result or output using assert statements
        response.andDo(print()).
                andExpect(status().isMovedPermanently());

    }
