package com.goonok.equalbangla;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EqualBanglaApplication {

    public static void main(String[] args) {
        SpringApplication.run(EqualBanglaApplication.class, args);
    }

}
