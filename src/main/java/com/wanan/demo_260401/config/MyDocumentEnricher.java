package com.wanan.demo_260401.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.KeywordMetadataEnricher;
import org.springframework.ai.transformer.SummaryMetadataEnricher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyDocumentEnricher {

    private final ChatModel chatModel;

    public MyDocumentEnricher(ChatModel dashscopeChatModel) {
        this.chatModel = dashscopeChatModel;
    }

    public List<Document> enrichDocuments(List<Document> documents) {
        KeywordMetadataEnricher metadataEnricher = new KeywordMetadataEnricher(this.chatModel, 5);
        return metadataEnricher.apply(documents);
    }

    public List<Document> enricherDocumentsSummary(List<Document> documents) {
        SummaryMetadataEnricher enricher = new SummaryMetadataEnricher(chatModel,
                List.of(SummaryMetadataEnricher.SummaryType.PREVIOUS,
                        SummaryMetadataEnricher.SummaryType.CURRENT,
                        SummaryMetadataEnricher.SummaryType.NEXT)
        );
        return enricher.apply(documents);
    }
}
