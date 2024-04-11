package com.shorten.url;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;

@ActiveProfiles("test")
public class AbstractContainerBaseTest {

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
}
