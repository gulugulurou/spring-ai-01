package com.wanan.demo_260401.app;

import com.wanan.demo_260401.config.MyDocumentEnricher;
import com.wanan.demo_260401.config.MyTokenTextSplitter;
import com.wanan.demo_260401.reg.LoveAppDocumentLoader;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class LoveAppDocumentLoaderTest {

    @Autowired
    private LoveAppDocumentLoader loveAppDocumentLoader;
    @Autowired
    private MyTokenTextSplitter myTokenTextSplitter;

    @Autowired
    private MyDocumentEnricher myDocumentEnricher;

    @Test
    void loadMarkdowns() {
        List<Document> documents = loveAppDocumentLoader.loadMarkdowns();
        List<Document> documents1 = myTokenTextSplitter.splitCustomized(documents);
        System.out.println(documents1);
    }

    @Test
    void testEnricher() {
        List<Document> documents = loveAppDocumentLoader.loadMarkdowns();
        List<Document> documents1 = myDocumentEnricher.enrichDocuments(documents);
        List<Document> documents2 = myDocumentEnricher.enricherDocumentsSummary(documents);
        System.out.println(documents1);
        System.out.println(documents2);
    }
}