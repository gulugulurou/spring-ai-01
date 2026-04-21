package com.wanan.demo_260401.reg;

import jakarta.annotation.Resource;
import org.springframework.ai.document.DefaultContentFormatter;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VectorStoreFormatter {

    @Resource(name = "pgVectorVectorStore")
    private VectorStore pgVectorVectorStoreConfig;

    // 1. 定义格式化器（一次创建，反复用）
    private final DefaultContentFormatter formatter = DefaultContentFormatter.builder()
            .withMetadataTemplate("{key}: {value}")
            .withMetadataSeparator("\n")
            .withTextTemplate("{metadata_string}\n\n{content}")
            .withExcludedInferenceMetadataKeys("embedding", "vector_id")
            .withExcludedEmbedMetadataKeys("source_url", "timestamp")
            .build();

    // 你的RAG方法
    public String ask(String question) {
        // 1. 从向量库检索相似内容
        List<Document> documents = pgVectorVectorStoreConfig.similaritySearch(question);

        // 2. 把检索到的文档 → 格式化
        StringBuilder context = new StringBuilder();
        for (Document doc : documents) {
            String formatted = formatter.format(doc, MetadataMode.INFERENCE);
            context.append(formatted).append("\n\n----------------\n\n");
        }

        // 3. 拼接prompt给AI
        String prompt = "根据以下资料回答：\n" + context + "\n用户问题：" + question;

        // 4. 调用AI返回结果
        return "这里替换成你的AI调用";
    }

}
