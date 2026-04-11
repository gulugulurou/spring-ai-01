package com.wanan.demo_260401;

import org.springframework.ai.autoconfigure.vectorstore.pgvector.PgVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = PgVectorStoreAutoConfiguration.class)
public class Demo260401Application {

    public static void main(String[] args) {
        SpringApplication.run(Demo260401Application.class, args);
    }

}