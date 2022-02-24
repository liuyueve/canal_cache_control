package com.voi.study.canal.cache_control;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan("com.voi.study.canal.cache_control.entity")
@SpringBootApplication
public class CacheControlApplication {

    public static void main(String[] args) {
        SpringApplication.run(CacheControlApplication.class, args);
    }

}
