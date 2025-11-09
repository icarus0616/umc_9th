package com.example.umc9th.global.notifier;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class WebhookNotifier {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    @Value("${webhook.discord.url:}")
    private String discordWebhookUrl;

    public WebhookNotifier(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

/*

    @Value("${spring.profiles.active:local}")
    private String activeProfile;
*/

    /**
     * Discord Webhook으로 메시지 전송
     */
    public void sendDiscordMessage(String message) {
/*
        // 🚫 로컬 환경에서는 전송하지 않음
        if ("local".equals(activeProfile)) {
            System.out.println("🚫 [LOCAL] Discord 알림 전송 생략");
            return;
        }
*/
        System.out.println("🔍 [DEBUG] Discord Webhook URL = " + discordWebhookUrl);


        try {
            log.info("🔍 [DEBUG] Discord Webhook URL = {}", discordWebhookUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // ✅ payload를 안전하게 JSON으로 변환
            Map<String, Object> payload = new HashMap<>();
            payload.put("content", message);

            String json = objectMapper.writeValueAsString(payload);
            HttpEntity<String> entity = new HttpEntity<>(json, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(discordWebhookUrl, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ Discord 알림 전송 성공");
            } else {
                log.error("❌ Discord 응답 오류: {} - {}", response.getStatusCode(), response.getBody());
            }

        } catch (Exception e) {
            log.error("❌ Discord 알림 전송 실패: {}", e.getMessage());
        }
    }
}
