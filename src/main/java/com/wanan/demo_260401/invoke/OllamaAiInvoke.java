package com.wanan.demo_260401.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// 取消注释即可在 SpringBoot 项目启动时执行
//@Component
public class OllamaAiInvoke implements CommandLineRunner {

//    @Resource
    private ChatModel ollamaChatModel;

    @Override
    public void run(String... args) throws Exception {
        AssistantMessage output = ollamaChatModel.call(new Prompt("你好，我是鱼皮"))
                .getResult()
                .getOutput();
        System.out.println(output.getText());

        // 基础用法 ChatModel
        ChatResponse response = ollamaChatModel.call(new Prompt("提问信息？"));

        // ChatClient
        ChatClient client = ChatClient.builder(ollamaChatModel)
                .defaultSystem("系统的角色设定")
                .build();

        String content = client.prompt().user("用户提问").call().content();
        System.out.println(content);
    }
}
