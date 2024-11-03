package com.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing // Enables auditing annotations like @CreatedDate
public class AdminComponentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminComponentApplication.class, args);
    }
}
