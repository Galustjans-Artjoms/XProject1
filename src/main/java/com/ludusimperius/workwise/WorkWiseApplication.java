package com.ludusimperius.workwise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.ludusimperius.workwise")
@EnableJpaRepositories("com.ludusimperius.workwise")
public class WorkWiseApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkWiseApplication.class, args);
    }
} 