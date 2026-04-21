package com.wanan.demo_260401.reg;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MyDocumentWriterTest {

    @Resource(name = "pgVectorVectorStore")
    private VectorStore pgVectorVectorStoreConfig;


    @Autowired
    private MyDocumentWriter myDocumentWriter;

    @Test
    void write() {
        List<Document> documents = pgVectorVectorStoreConfig.similaritySearch("我已经结婚了，但是婚后关系不太亲密，怎么办？");
        myDocumentWriter.write(documents);
    }
}