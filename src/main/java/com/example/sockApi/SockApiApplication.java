package com.example.sockApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class SockApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SockApiApplication.class, args);
    }

}
