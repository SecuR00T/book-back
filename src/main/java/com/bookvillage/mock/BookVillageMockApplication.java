package com.bookvillage.mock;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ComponentScan({"com.bookvillage.mock", "com.bookvillage.backend"})
public class BookVillageMockApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookVillageMockApplication.class, args);
    }
}
