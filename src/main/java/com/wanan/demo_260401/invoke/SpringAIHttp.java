package com.wanan.demo_260401.invoke;

import com.wanan.demo_260401.TestApiKey;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class SpringAIHttp {
    public static void main(String[] args) {
        String key = TestApiKey.API_KEY;
        String url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + key);
        headers.set("Content-Type", "application/json");
        headers.set("X-DashScope-SSE", "enable");

        String body = """
                {"model":"deepseek-v3.2","input":{"messages":[{"role":"user","content":"你是谁？"}]},
                "parameters":{"enable_thinking":true,"incremental_output":true,"result_format":"message"}}
                """;

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        System.out.println(response.getBody());
    }
}