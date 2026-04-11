package com.wanan.demo_260401.app;

import com.esotericsoftware.kryo.io.Input;
import com.wanan.demo_260401.domain.Food;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;


@Component
@Slf4j
public class LoveApp {

    @Autowired
    private ChatClient chatClient;

    @Resource
    private ChatModel ollamaChatModel;

    @Resource
    private ChatModel dashscopeChatModel;

    // 从类路径资源加载系统提示模板
    @Value("classpath:/prompts/system-message.st")
    private org.springframework.core.io.Resource systemResource;

    // 从类路径资源加载系统提示模板
    @Value("classpath:/prompts/system-message.st")
    private org.springframework.core.io.Resource userResource;

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

//    public LoveApp(ChatModel dashscopeChatModel) {
//        ChatMemory chatMemory = new InMemoryChatMemory();
////        String fileDir = System.getProperty("user.dir") + "/chat-memory";
////        ChatMemory chatMemory = new FileBaseChatMemory(fileDir);
//
//        chatClient = ChatClient.builder(dashscopeChatModel)
//                .defaultSystem(SYSTEM_PROMPT)
//                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
////                .defaultAdvisors(new ReReadingAdvisor())
//                .build();
//    }

    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient.prompt(chatId)
                .user(message)
                .advisors(
                        spec ->
                                spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                                    .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new MyLoggerAdvisor())
                .advisors(new MyLoggerAdvisor2())
                .call()
                .chatResponse();

        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    public String doChat1(String message, String chatId) {
        ChatResponse chatResponse = chatClient.prompt(chatId)
                .user(message)
                .advisors(
                        spec ->
                                spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                                    .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 1))
                .advisors(new ReReadingAdvisor())
                .advisors(new MyLoggerAdvisor())
                .call()
                .chatResponse();

        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    public Food doChat2(String message, String chatId) {
        Food entity = chatClient.prompt(chatId)
                .user(u -> u.text(message))
                .advisors(
                        spec ->
                                spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 1))
                .advisors(new ReReadingAdvisor())
                .advisors(new MyLoggerAdvisor())
                .call()
                .entity(Food.class);
        return entity;
    }

    public List<Food> doChat3(String message, String chatId) {
        List<Food> foods = chatClient.prompt(chatId)
                .user(u -> u.text(message))
                .advisors(
                        spec ->
                                spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 1))
                .advisors(new ReReadingAdvisor())
                .advisors(new MyLoggerAdvisor())
                .call()
                .entity(new ParameterizedTypeReference<List<Food>>() {
                });
        return foods;
    }

    public Map<String, Object> doChat4(String message, String chatId) {
        Map<String, Object> result = chatClient.prompt()
                .user(
                        u -> u.text("Provide me a List of {subject}")
                        .param("subject", "an array of numbers from 1 to 9 under they key name 'numbers'"))
                .call()
                .entity(new ParameterizedTypeReference<Map<String, Object>>() {});
        return result;
    }

    public List<String> doChat5(String message, String chatId) {
        List<String> flavors = chatClient.prompt()
                .user(u -> u.text("List five {subject}")
                        .param("subject", "ice cream flavors"))
                .call()
                .entity(new ListOutputConverter(new DefaultConversionService()));
        return flavors;
    }

    record LoveReport(String title, List<String> suggestions) {}

    public LoveReport doChatWithReport(String message, String chatId) {
        LoveReport loveReport = chatClient.prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(LoveReport.class);
        return loveReport;
    }

    public void doPrompt3() {
        String userText = """
    Tell me about three famous pirates from the Golden Age of Piracy and why they did.
    Write at least a sentence for each pirate.
    """;

        Message userMessage = new UserMessage(userText);

        String systemText = """
          You are a helpful AI assistant that helps people find information.
          Your name is {name}
          You should reply to the user's request with your name and also in the style of a {voice}.
          """;

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemText);
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", "海贼王粉丝", "voice", "路飞"));

        Prompt prompt = new Prompt(List.of(userMessage, systemMessage));
        List<Generation> result = dashscopeChatModel.call(prompt).getResults();
        System.out.println(result);
    }

    public void doResource4() {

        String userText = """
    Tell me about three famous pirates from the Golden Age of Piracy and why they did.
    Write at least a sentence for each pirate.
    """;
        Message userMessage = new UserMessage(userText);

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemResource);

        Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", "海贼王粉丝", "voice", "路飞"));

        Prompt prompt = new Prompt(List.of(userMessage, systemMessage));
        List<Generation> result = dashscopeChatModel.call(prompt).getResults();
        System.out.println(result);
    }

}
