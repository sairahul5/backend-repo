package com.qpmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class QpManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(QpManagementApplication.class, args);
    }
}
