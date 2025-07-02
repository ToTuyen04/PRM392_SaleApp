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
public class AIService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateReply(String userMessage) {
        String context = """
                Thông tin hệ thống:
                Đây là ứng dụng bán hàng Android tên là SalesApp.
                Các tính năng:
                - Đăng ký, đăng nhập
                - Xem sản phẩm
                - Giỏ hàng, đặt hàng, thanh toán
                - Chat với AI hoặc nhân viên
                - Xem vị trí cửa hàng trên bản đồ

                Hãy phản hồi ngắn gọn, rõ ràng. Nếu người dùng yêu cầu gặp nhân viên, hãy nhắn rằng bạn sẽ chuyển tiếp họ.
                """;

        String apiUrl = geminiApiUrl + "?key=" + geminiApiKey;

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("role", "user", "parts", List.of(Map.of("text", context))),
                        Map.of("role", "user", "parts", List.of(Map.of("text", userMessage)))
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");

            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, String>> parts = (List<Map<String, String>>) content.get("parts");
                return parts.get(0).get("text");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Xin lỗi, tôi chưa thể phản hồi lúc này.";
    }
}
