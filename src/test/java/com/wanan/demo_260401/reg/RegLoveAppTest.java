package com.wanan.demo_260401.reg;

import com.wanan.demo_260401.config.MyDocumentEnricher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RegLoveAppTest {

    @Autowired
    private RegLoveApp regLoveApp;


    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "我已经结婚了，但是婚后关系不太亲密，怎么办？";
        String answer =  regLoveApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void MyJsonReader() {
        ClassPathResource classPathResource = new ClassPathResource("readRag/products.json");
        Resource resource = null;
        try {
            resource = new FileSystemResource(new File(classPathResource.getURI()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JsonReader jsonReader = new JsonReader(resource);
        List<Document> documents = jsonReader.get();
        System.out.println(documents);


        JsonReader jsonReader1 = new JsonReader(resource, "id", "brand");
        List<Document> documents1 = jsonReader1.get();
//        List<Document> documents1 = jsonReader1.get("items");
        System.out.println(documents1);
    }

}