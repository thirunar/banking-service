package com.finmid.bankingservice;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class ContainerInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Container
    private static PostgreSQLContainer container = new PostgreSQLContainer("postgres:9.6.12");

    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        container.start();
        TestPropertyValues.of(
                "spring.datasource.url=" + container.getJdbcUrl(),
                "spring.datasource.username=" + container.getUsername(),
                "spring.datasource.password=" + container.getPassword()
        ).applyTo(configurableApplicationContext.getEnvironment());
    }
}