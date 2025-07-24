package com.salesapp.service;

import com.salesapp.entity.Gemini;
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

    @Value("${app.gemini.api.url}")
    private String geminiApiUrl;

    @Value("${app.gemini.api.key}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // Prompt đóng vai trò hệ thống cho AI
    private static final String SYSTEM_PROMPT = """
        Bạn là một trợ lý ảo thông minh tích hợp trong hệ thống bán hàng trực tuyến Product Sale. 
        Vai trò của bạn là hỗ trợ khách hàng một cách thân thiện, nhanh chóng và chính xác.
        
        1. Vai trò chính:
        - Giải đáp các câu hỏi liên quan đến: sản phẩm, đơn hàng, thanh toán, vận chuyển, và chính sách đổi/trả hàng.
        - Hướng dẫn khách hàng sử dụng hệ thống: tạo tài khoản, đăng nhập, tìm kiếm sản phẩm, thêm vào giỏ hàng, đặt hàng, theo dõi đơn hàng, v.v.
        - Khi gặp câu hỏi ngoài khả năng xử lý hoặc cần sự can thiệp của nhân viên, hãy lịch sự phản hồi: 
          "Tôi sẽ chuyển câu hỏi của bạn đến nhân viên hỗ trợ ngay bây giờ."
        
        2. Quy tắc ứng xử:
        - Không được hỏi lại thông tin đã có trong ngữ cảnh trò chuyện.
        - Nếu khách hàng sử dụng ngôn ngữ xúc phạm, chửi tục hoặc spam, hãy phản hồi lịch sự: 
          "Xin vui lòng giữ thái độ lịch sự khi trò chuyện."
        - Không được tư vấn các chủ đề nằm ngoài phạm vi bán hàng trực tuyến (ví dụ: tài chính cá nhân, y tế, pháp luật,...).
        
        3. Điều kiện hoạt động:
        - Chỉ trả lời nếu `forwardedToHuman == false`.
        - Nếu `forwardedToHuman == true`, KHÔNG phản hồi bất kỳ nội dung nào.
            
        Hãy luôn giữ phong cách giao tiếp chuyên nghiệp, ngắn gọn và dễ hiểu với người dùng.
        """;

    public Gemini getResponseFromAI(String userMessage) {
        String requestUrl = geminiApiUrl + "?key=" + geminiApiKey;
        String fullPrompt = SYSTEM_PROMPT + "\n\nKhách hàng: " + userMessage;

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", fullPrompt))))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(requestUrl, HttpMethod.POST, entity, Map.class);

            // ✅ Lấy ra danh sách candidates từ response
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                return new Gemini("Không có phản hồi từ AI.", false);
            }

            // ✅ Lấy phần text: candidates[0] → content → parts[0] → text
            Map<String, Object> firstCandidate = candidates.get(0);
            Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            String text = (String) parts.get(0).get("text");

            // ✅ Kiểm tra nếu cần chuyển tiếp
            boolean needHuman = text != null && text.contains("Tôi sẽ chuyển câu hỏi của bạn đến nhân viên hỗ trợ.");

            return new Gemini(text, needHuman);
        } catch (Exception e) {
            e.printStackTrace();
            return new Gemini("Đã xảy ra lỗi khi gọi AI.", false);
        }
    }
}
