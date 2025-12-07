package com.dme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.dme.persistence",
    "com.dme.infrastructure",
    "com.dme.distributed",
    "com.dme.soa",
    "com.dme"
})
public class DmeSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(DmeSystemApplication.class, args);
    }
}
