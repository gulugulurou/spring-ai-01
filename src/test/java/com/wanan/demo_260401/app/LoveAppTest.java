package com.wanan.demo_260401.app;

import jakarta.annotation.Resource;
import lombok.Value;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.converter.StructuredOutputConverter;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Test
    void doChat() {

        String chatId = UUID.randomUUID().toString();
        System.out.println(chatId);
        // 第一轮
        String message = "你好，我是程序员鱼皮";
        String answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
//        // 第二轮
//        message = "我想让另一半（刘亦菲）更爱我";
//        answer = loveApp.doChat(message, chatId);
//        Assertions.assertNotNull(answer);
//        // 第三轮
//        message = "我的另一半叫什么来着？刚跟你说过，帮我回忆一下";
//        answer = loveApp.doChat(message, chatId);
//        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是程序员鱼皮，我想让另一半（编程导航）更爱我，但我不知道该怎么做";
        LoveApp.LoveReport loveReport = loveApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(loveReport);
    }

    @Test
    void testPrompt() {
        String promptStr = "今天的晚餐是{food}, 价格是{price}元";
        PromptTemplate promptTemplate = new PromptTemplate(promptStr);

        HashMap<String, Object> map = new HashMap<>();
        map.put("food", "耙耙柑");
        map.put("price", "20");
        System.out.println(promptTemplate.render(map));
    }

    @Test
    void testPrompt2() {
        String promptStr = "今天的晚餐是{food}, 价格是{price}元";
        PromptTemplate promptTemplate = new PromptTemplate(promptStr);

        HashMap<String, Object> map = new HashMap<>();
        map.put("food", "耙耙柑");
        map.put("price", "20");
        System.out.println(promptTemplate.render(map));
    }

    @Test
    void testPrompt3() {
        loveApp.doPrompt3();
    }

    @Test
    void doResource4() {
        loveApp.doResource4();
    }

}


