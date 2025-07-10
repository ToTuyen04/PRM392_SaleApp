package com.salesapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getResponseFromAI(String userMessage) {
        String requestUrl = geminiApiUrl + "?key=" + geminiApiKey;

        // Build request body
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", userMessage)
                        ))
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    requestUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            if (body == null || !body.containsKey("candidates")) {
                return "Không nhận được phản hồi từ AI.";
            }

            // Parse candidates
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
            if (candidates.isEmpty()) return "Không có phản hồi từ AI.";

            Map<String, Object> candidate = candidates.get(0);

            Map<String, Object> content = (Map<String, Object>) candidate.get("content");
            if (content == null || !content.containsKey("parts")) {
                return "Phản hồi AI không hợp lệ.";
            }

            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            if (parts.isEmpty() || !parts.get(0).containsKey("text")) {
                return "AI không gửi nội dung nào.";
            }

            return (String) parts.get(0).get("text");

        } catch (Exception e) {
            e.printStackTrace();
            return "Xin lỗi, hiện tại AI đang bận. Vui lòng thử lại sau.";
        }
    }
}
