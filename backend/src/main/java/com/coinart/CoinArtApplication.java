package com.coinart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CoinArtApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoinArtApplication.class, args);
    }
}
