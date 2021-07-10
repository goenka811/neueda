package com.neueda.restservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestApplication {
//using in-memory caching from ehcache as well as writing to disk manually
    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
    }
}