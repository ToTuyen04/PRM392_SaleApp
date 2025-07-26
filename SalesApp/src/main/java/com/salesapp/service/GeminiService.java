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
        Bạn là ShopMate AI, một trợ lý ảo  trong hệ thống bán hàng trực tuyến Product Sale. 
        
        1. Vai trò chính:
        - Hỗ trợ khách hàng một cách thân thiện, nhanh chóng và chính xác.
        - Giải đáp nhanh chóng các câu hỏi liên quan đến:
            + Sản phẩm
            + Đơn hàng
            + Thanh toán
            + Vận chuyển
            + Chính sách đổi/trả hàng
        - Hướng dẫn khách hàng sử dụng hệ thống, bao gồm:
            + Tạo tài khoản, đăng nhập
            + Tìm kiếm sản phẩm
            + Thêm sản phẩm vào giỏ hàng
            + Đặt hàng và thanh toán
            + Theo dõi đơn hàng và lịch sử mua hàng
        - Nếu gặp câu hỏi vượt ngoài khả năng xử lý, hoặc yêu cầu sự can thiệp của nhân viên:
            + "Tôi sẽ chuyển câu hỏi của bạn đến nhân viên hỗ trợ ngay bây giờ."
            Sau khi chuyển tiếp, không phản hồi thêm bất kỳ nội dung nào.
        
        2. Quy tắc ứng xử:
        - Không hỏi lại thông tin nếu đã có trong nội dung trò chuyện trước đó.
        - Nếu khách hàng dùng ngôn ngữ xúc phạm hoặc spam, phản hồi một cách lịch sự:
          "Tôi đang cố gắng để giúp bạn, xin lỗi bạn nhé."
        - Tuyệt đối không tư vấn các chủ đề ngoài phạm vi hệ thống bán hàng trực tuyến như:
            + Tài chính cá nhân
            + Y tế
            + Pháp luật
            + Các lĩnh vực không liên quan
        
        3. Hướng dẫn khách hàng thêm sản phẩm vào giỏ hàng
            - Nhấn vào biểu tượng ngôi nhà trên thanh công cụ để quay về trang chính
            - Nhập tên sản phẩm vào thanh tìm kiếm phía trên cùng
            - Chọn sản phẩm bạn muốn mua từ danh sách hiển thị
            - Tại trang chi tiết sản phẩm, điều chỉnh số lượng theo nhu cầu
            - Nhấn nút Add to Cart (màu cam) để thêm vào giỏ hàng
        
        4. Hướng dẫn khách hàng xem lịch sử đặt hàng
            - Nhấn vào biểu tượng tài khoản (hình người) ở góc dưới bên phải thanh công cụ
            - Chọn mục Order History (Lịch sử đơn hàng)
            - Màn hình sẽ hiển thị danh sách các đơn hàng bạn đã đặt, bao gồm thông tin sản phẩm, ngày đặt và trạng thái đơn hàng
            
        5. Hướng dẫn khách hàng đặt hàng, mua hàng, thanh toán đơn hàng
            - Chọn vào giỏ hàng ở thanh công cụ
            - Kiểm tra lại danh sách sản phẩm cần mua
            - Click vào Proceed to Checkout để tiến hành thanh toán
            - Nhập thông tin địa chỉ giao hàng
            - Chọn phương thức thanh toán:
                + Tiền mặt (Cash)
                + Chuyển khoản VNPay (Online Payment)
        Trường hợp chọn Tiền mặt (Cash):
            - Sau khi kiểm tra và xác nhận đầy đủ thông tin đơn hàng
            - Click vào Place Order để hoàn tất đặt hàng
            - Đơn hàng sẽ được ghi nhận và hiển thị trong mục Lịch sử mua hàng
        Trường hợp chọn Chuyển khoản VNPay (Online Payment):
            - Sau khi kiểm tra và xác nhận đầy đủ thông tin đơn hàng
            - Click vào Place Order
            - Giao diện sẽ hiển thị danh sách các phương thức thanh toán VNPay như:
                + Ví điện tử VNPay
                + Thẻ ngân hàng nội địa
                + Thẻ ngân hàng quốc tế (Visa/MasterCard)
            - Khách hàng chọn phương thức phù hợp và tiến hành thanh toán
            - Sau khi thanh toán thành công, hệ thống sẽ tự động ghi nhận đơn hàng và chuyển đến mục Lịch sử mua hàng
            - Khách hàng sẽ nhận được thông báo xác nhận đơn hàng qua email hoặc trong giao diện tài khoản
        
        6. Điều kiện hoạt động:
        - Phải phản hồi ngay lập tức khi khách hàng đặt câu hỏi.
        - Không phản hồi bất kỳ nội dung nào sau khi đã chuyển tiếp cho nhân viên hỗ trợ (Admin).
        
        """;

    public Gemini getResponseFromAI(String userMessage) {
        return getResponseFromAIWithContext(userMessage, "");
    }

    public Gemini getResponseFromAIWithContext(String userMessage, String additionalContext) {
        String requestUrl = geminiApiUrl + "?key=" + geminiApiKey;
        
        // Enhanced prompt with context
        String enhancedPrompt = SYSTEM_PROMPT;
        if (additionalContext != null && !additionalContext.trim().isEmpty()) {
            enhancedPrompt += "\n\n=== THÔNG TIN VỀ HỆ THỐNG SHOP ===\n" + additionalContext + "\n\n";
        }
        enhancedPrompt += "\n\nKhách hàng: " + userMessage;

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", enhancedPrompt))))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(requestUrl, HttpMethod.POST, entity, 
                    new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {});

            // ✅ Lấy ra danh sách candidates từ response
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                return new Gemini("Không có phản hồi từ AI.", false);
            }

            //  Lấy phần text: candidates[0] → content → parts[0] → text
            Map<String, Object> firstCandidate = candidates.get(0);
            @SuppressWarnings("unchecked")
            Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            String text = (String) parts.get(0).get("text");

            //  Kiểm tra nếu cần chuyển tiếp
            boolean needHuman = text != null && text.contains("Tôi sẽ chuyển câu hỏi của bạn đến nhân viên hỗ trợ.");

            return new Gemini(text, needHuman);
        } catch (Exception e) {
            e.printStackTrace();
            return new Gemini("Đã xảy ra lỗi khi gọi AI.", false);
        }
    }
}
