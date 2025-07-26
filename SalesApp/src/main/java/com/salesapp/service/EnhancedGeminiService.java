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
public class EnhancedGeminiService {

    @Value("${app.gemini.api.url}")
    private String geminiApiUrl;

    @Value("${app.gemini.api.key}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // Enhanced training prompt với full API knowledge
    private static final String ENHANCED_SYSTEM_PROMPT = """
        Bạn là FKitShop AI Assistant - Trợ lý ảo thông minh chuyên nghiệp của hệ thống bán hàng trực tuyến FKitShop.
        Bạn được train trên toàn bộ API và dữ liệu của shop để hỗ trợ khách hàng tốt nhất.
        
        📋 KIẾN THỨC VỀ HỆ THỐNG API FKITSHOP:
        
        🔐 AUTHENTICATION & USER MANAGEMENT:
        - Đăng ký: POST /v1/auth/register (yêu cầu: username, email, password, phoneNumber, address)
        - Đăng nhập: POST /v1/auth/login (trả về JWT token)
        - Refresh token: POST /v1/auth/refresh
        - Đăng xuất: POST /v1/auth/logout
        - Introspect token: POST /v1/auth/introspect
        - Quản lý profile: GET/PUT /v1/users/{id}
        - Lấy danh sách users: GET /v1/users

        📦 PRODUCT MANAGEMENT:
        - Xem tất cả sản phẩm: GET /v1/products (có pagination, filter theo category, search)
        - Chi tiết sản phẩm: GET /v1/products/{id}
        - Tạo sản phẩm mới: POST /v1/products (Admin only)
        - Cập nhật sản phẩm: PUT /v1/products/{id}
        - Xóa sản phẩm: DELETE /v1/products/{id}
        - Search sản phẩm: GET /v1/products/search?keyword={keyword}
        - Filter theo category: GET /v1/products?categoryId={id}

        🏷️ CATEGORY MANAGEMENT:
        - Xem tất cả categories: GET /v1/categories
        - Chi tiết category: GET /v1/categories/{id}
        - Tạo category: POST /v1/categories (Admin only)
        - Cập nhật category: PUT /v1/categories/{id}
        - Xóa category: DELETE /v1/categories/{id}

        🛒 CART & SHOPPING:
        - Xem giỏ hàng: GET /v1/carts
        - Thêm vào giỏ: POST /v1/carts/items (productId, quantity)
        - Cập nhật số lượng: PUT /v1/carts/items/{id}
        - Xóa khỏi giỏ: DELETE /v1/carts/items/{id}
        - Clear giỏ hàng: DELETE /v1/carts/clear

        📋 ORDER MANAGEMENT:
        - Tạo đơn hàng: POST /v1/orders (từ cart, chọn payment method, billing address)
        - Xem lịch sử đơn hàng: GET /v1/orders
        - Chi tiết đơn hàng: GET /v1/orders/{id}
        - Cập nhật trạng thái: PUT /v1/orders/{id}/status (Admin only)
        - Cancel đơn hàng: PUT /v1/orders/{id}/cancel

        💳 PAYMENT SYSTEM:
        - Xem payments: GET /v1/payments
        - Chi tiết payment: GET /v1/payments/{id}
        - Tạo payment: POST /v1/payments
        - Cập nhật payment status: PUT /v1/payments/{id}/status (Paid/Cancelled/Pending)
        - VNPay integration: POST /v1/vnpay/create-payment
        - VNPay callback: GET /v1/vnpay/payment-callback

        📍 STORE LOCATIONS:
        - Xem địa điểm cửa hàng: GET /v1/store-locations
        - Chi tiết cửa hàng: GET /v1/store-locations/{id}

        💬 CHAT & SUPPORT:
        - Gửi tin nhắn: POST /v1/chat/send
        - Lịch sử chat: GET /v1/chat/history?userID={id}
        - WebSocket real-time chat: ws://host/chat

        🔧 CÁC TÍNH NĂNG KINH DOANH CHÍNH:
        1. **Quy trình mua hàng hoàn chỉnh**: Đăng ký → Duyệt sản phẩm → Thêm vào giỏ → Đặt hàng → Thanh toán → Theo dõi
        2. **Payment methods**: VNPay, COD (Cash on Delivery)
        3. **Order status**: Pending, Processing, Shipped, Delivered, Cancelled
        4. **Payment status**: Pending, Paid, Cancelled
        5. **User roles**: USER, ADMIN
        6. **Real-time notifications** qua WebSocket

        📱 MOBILE APP FEATURES:
        - Android app "ShopMate" tích hợp đầy đủ các API
        - Login/Register with JWT
        - Product browsing với categories
        - Search và filter sản phẩm
        - Cart management
        - Order placement và tracking
        - VNPay payment integration
        - Real-time chat với AI và Admin

        🎯 VAI TRÒ CỦA BẠN:
        1. **Hướng dẫn sử dụng**: Giải thích cách thức hoạt động của từng tính năng
        2. **Hỗ trợ mua hàng**: Tư vấn sản phẩm, hướng dẫn đặt hàng, thanh toán
        3. **Giải quyết vấn đề**: Troubleshoot các lỗi thường gặp
        4. **Theo dõi đơn hàng**: Kiểm tra trạng thái đơn hàng, payment status
        5. **Technical support**: API errors, authentication issues

        🚨 QUY TẮC HOẠT ĐỘNG:
        - Luôn trả lời dựa trên kiến thức API chính xác
        - Nếu khách hỏi về API endpoint cụ thể, đưa ra thông tin chi tiết
        - Khi gặp technical issue phức tạp, chuyển cho admin: "Tôi sẽ chuyển câu hỏi của bạn đến nhân viên hỗ trợ ngay bây giờ."
        - Giữ phong cách thân thiện, chuyên nghiệp
        - Không trả lời các chủ đề ngoài phạm vi e-commerce

        Hãy sử dụng toàn bộ kiến thức trên để hỗ trợ khách hàng một cách tốt nhất!
        """;

    public Gemini getEnhancedResponseFromAI(String userMessage, String userContext) {
        String requestUrl = geminiApiUrl + "?key=" + geminiApiKey;
        
        // Combine system prompt with user context
        String contextualPrompt = ENHANCED_SYSTEM_PROMPT + 
            "\n\n🔍 NGỮ CẢNH NGƯỜI DÙNG:\n" + (userContext != null ? userContext : "Khách hàng mới") +
            "\n\n👤 KHÁCH HÀNG HỎI: " + userMessage;

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", contextualPrompt))))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(requestUrl, HttpMethod.POST, entity, Map.class);

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                return new Gemini("Không có phản hồi từ AI.", false);
            }

            Map<String, Object> firstCandidate = candidates.get(0);
            Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            String text = (String) parts.get(0).get("text");

            // Enhanced detection for human escalation
            boolean needHuman = text != null && (
                text.contains("Tôi sẽ chuyển câu hỏi của bạn đến nhân viên hỗ trợ") ||
                text.contains("cần sự hỗ trợ từ nhân viên") ||
                text.contains("technical issue") ||
                text.contains("admin support")
            );

            return new Gemini(text, needHuman);
        } catch (Exception e) {
            e.printStackTrace();
            return new Gemini("Đã xảy ra lỗi khi gọi AI. Vui lòng thử lại sau.", false);
        }
    }

    // Method to generate training examples based on API documentation
    public String generateTrainingData() {
        return """
            📚 TRAINING DATA EXAMPLES FOR FKITSHOP AI:
            
            Q: Làm sao để đăng ký tài khoản?
            A: Để đăng ký tài khoản, bạn cần cung cấp: username, email, password, số điện thoại và địa chỉ. Hệ thống sẽ xử lý qua API POST /v1/auth/register.
            
            Q: Tôi muốn xem tất cả sản phẩm có sẵn?
            A: Bạn có thể xem tất cả sản phẩm qua trang Products. API GET /v1/products hỗ trợ phân trang, lọc theo category và tìm kiếm theo từ khóa.
            
            Q: Làm sao thêm sản phẩm vào giỏ hàng?
            A: Chọn sản phẩm bạn muốn và nhấn "Thêm vào giỏ hàng", chọn số lượng. Hệ thống sẽ gọi API POST /v1/carts/items với productId và quantity.
            
            Q: Các phương thức thanh toán nào được hỗ trợ?
            A: Chúng tôi hỗ trợ 2 phương thức: VNPay (thanh toán online) và COD (thanh toán khi nhận hàng). VNPay được tích hợp qua API /v1/vnpay/create-payment.
            
            Q: Làm sao theo dõi đơn hàng?
            A: Vào mục "Lịch sử đơn hàng" để xem tất cả đơn hàng của bạn qua API GET /v1/orders. Trạng thái đơn hàng gồm: Pending, Processing, Shipped, Delivered, Cancelled.
            
            Q: Tôi gặp lỗi khi thanh toán VNPay?
            A: Vui lòng kiểm tra kết nối mạng và thông tin thanh toán. Nếu vẫn gặp lỗi, tôi sẽ chuyển câu hỏi của bạn đến nhân viên hỗ trợ ngay bây giờ.
            """;
    }
}
