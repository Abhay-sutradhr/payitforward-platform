package com.payitforward.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.payitforward.platform.model.entity")
@EnableJpaRepositories(basePackages = "com.payitforward.platform.repository")
public class PayItForwardPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayItForwardPlatformApplication.class, args);
    }
}
