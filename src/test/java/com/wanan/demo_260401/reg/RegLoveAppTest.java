package com.wanan.demo_260401.reg;

import com.wanan.demo_260401.config.MyDocumentEnricher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
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
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RegLoveAppTest {

    @Autowired
    private RegLoveApp regLoveApp;

    @Autowired
    private VectorStore loveAppVectorStore;

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

    @Autowired
    private ChatClient.Builder dashscopeChatModel;

    @Test
    void queryTest1() {
        Query query = new Query("啥是程序员鱼皮啊啊啊啊？");

        QueryTransformer queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(dashscopeChatModel)
                .build();

        Query transformedQuery = queryTransformer.transform(query);
        System.out.println(transformedQuery);
    }

    @Test
    void queryTest2() {
        Query query = new Query("hi, who is coder yupi? please answer me");

        QueryTransformer queryTransformer = TranslationQueryTransformer.builder()
                .chatClientBuilder(dashscopeChatModel)
                .targetLanguage("chinese")
                .build();

        Query transformedQuery = queryTransformer.transform(query);
        System.out.println(transformedQuery);
    }

    @Test
    void queryTest3() {
        // 当前问题上，添加历史的问答
        Query query = Query.builder()
                .text("编程导航有啥内容？")
                .history(new UserMessage("谁是程序员鱼皮？"),
                        new AssistantMessage("编程导航的创始人 codefather.cn"))
                .build();

        QueryTransformer queryTransformer = CompressionQueryTransformer.builder()
                .chatClientBuilder(dashscopeChatModel)
                .build();

        Query transformedQuery = queryTransformer.transform(query);
        System.out.println(transformedQuery);
    }

    @Test
    void queryTest4() {
        // 在你的问题上，生成多个相关的问题
        MultiQueryExpander queryExpander = MultiQueryExpander.builder()
                .chatClientBuilder(dashscopeChatModel)
                .numberOfQueries(3)
                .build();
        List<Query> queries = queryExpander.expand(new Query("啥是程序员鱼皮？他会啥？"));

        System.out.println(queries);
    }

    @Test
    void queryTest5() {
        // 用户问 1 句 → 自动生成 4~5 句同义句 → 一起去向量库检索 → 提高召回率
        MultiQueryExpander queryExpander = MultiQueryExpander.builder()
                .chatClientBuilder(dashscopeChatModel)
                .includeOriginal(true)  // true保留原始问题(false不保留) + 扩展问题
                .build();
        List<Query> queries = queryExpander.expand(new Query("谁是鱼皮"));
        System.out.println(queries);
    }

    @Test
    void queryTest6() {
        // 构造请求条件
        DocumentRetriever retriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(loveAppVectorStore)
                .similarityThreshold(0.2)
                .topK(5)
//                .filterExpression(new FilterExpressionBuilder()
//                        .eq("type", "web")
//                        .build())
                .build();

//        Query query = Query.builder()
//                .text("谁是鱼皮？")
//                .context(Map.of(VectorStoreDocumentRetriever.FILTER_EXPRESSION, "type == 'boy'"))
//                .build();

        List<Document> documents = retriever.retrieve(new Query("我已经结婚了，但是婚后关系不太亲密，怎么办？"));
        System.out.println(documents);
    }

    @jakarta.annotation.Resource(name = "dashscopeChatModel")
    private ChatModel dsChatModel;

    @Test
    void queryTest7() {
        // QuestionAnswerAdvisor写法1：
        QuestionAnswerAdvisor answerAdvisor = QuestionAnswerAdvisor.builder(loveAppVectorStore)
                .searchRequest(SearchRequest.builder().similarityThreshold(0.4).topK(5).build())
                .build();

        String content = ChatClient.builder(dsChatModel).build().prompt()
                .user("看着我的眼睛，回答我！")
                .advisors(answerAdvisor)
                .call()
                .content();
        System.out.println(content);

        // QuestionAnswerAdvisor写法2:
        ChatClient chatClient = ChatClient.builder(dsChatModel)
                .defaultAdvisors(QuestionAnswerAdvisor.builder(loveAppVectorStore)
                        .searchRequest(SearchRequest.builder().build())
                        .build())
                .build();

        ChatResponse response = chatClient.prompt()
                .advisors(new QuestionAnswerAdvisor(loveAppVectorStore))
                .user("我已经结婚了，但是婚后关系不太亲密，怎么办？")
                .call()
                .chatResponse();
        System.out.println(response);
    }

    @Test
    void queryTest8() {
        //  RetrievalAugmentationAdvisor：
        Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .queryTransformers(RewriteQueryTransformer.builder()
                        .chatClientBuilder(dashscopeChatModel.build().mutate())     // .mutate() 多例
                        .build())
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.50)
                        .vectorStore(loveAppVectorStore)
                        .build())
                .build();

    }

    @Test
    void queryTest9() {
        // 自定义 上下文为空
        Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.50)
                        .vectorStore(loveAppVectorStore)
                        .build())
                .queryAugmenter(ContextualQueryAugmenter.builder()
                        .allowEmptyContext(true)
                        .build())
                .build();
    }

}