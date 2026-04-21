package com.wanan.demo_260401.reg;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VectorStoreFormatterTest {

    @Autowired
    private VectorStoreFormatter vectorStoreFormatter;

    @Test
    void ask() {
        String ask = vectorStoreFormatter.ask("我已经结婚了，但是婚后关系不太亲密，怎么办？");
        System.out.println(ask);
    }
}