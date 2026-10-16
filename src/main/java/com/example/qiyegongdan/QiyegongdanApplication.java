package com.example.qiyegongdan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.qiyegongdan.mapper")
public class QiyegongdanApplication {

    public static void main(String[] args) {
        SpringApplication.run(QiyegongdanApplication.class, args);
    }

}