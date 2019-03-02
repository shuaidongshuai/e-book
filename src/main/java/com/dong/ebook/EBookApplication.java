package com.dong.ebook;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dong.ebook.dao")
public class EBookApplication {

    public static void main(String[] args) {
        SpringApplication.run(EBookApplication.class, args);
    }
}

