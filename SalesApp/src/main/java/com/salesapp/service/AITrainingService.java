package com.salesapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AITrainingService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final EnhancedGeminiService enhancedGeminiService;

    private static final String SWAGGER_API_DOCS_URL = "https://saleapp-mspd.onrender.com/v3/api-docs";

    /**
     * Fetch API documentation from Swagger and generate training data
     */
    @SuppressWarnings("unchecked")
    public String fetchAndGenerateTrainingData() {
        try {
            // Fetch API documentation from Swagger
            ResponseEntity<Map<String, Object>> response = restTemplate.getForEntity(SWAGGER_API_DOCS_URL, (Class<Map<String, Object>>) (Class<?>) Map.class);
            Map<String, Object> apiDocs = response.getBody();

            if (apiDocs == null) {
                return "Cannot fetch API documentation";
            }

            return generateComprehensiveTrainingData(apiDocs);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching API documentation: " + e.getMessage();
        }
    }

    /**
     * Generate comprehensive training data based on API documentation
     */
    @SuppressWarnings("unchecked")
    private String generateComprehensiveTrainingData(Map<String, Object> apiDocs) {
        StringBuilder trainingData = new StringBuilder();
        
        trainingData.append("🤖 FKITSHOP AI COMPREHENSIVE TRAINING DATA\n");
        trainingData.append("=========================================\n\n");

        // Extract API paths and generate training examples
        Map<String, Object> paths = (Map<String, Object>) apiDocs.get("paths");
        if (paths != null) {
            trainingData.append("📋 API ENDPOINTS KNOWLEDGE:\n\n");
            
            for (String path : paths.keySet()) {
                Map<String, Object> pathInfo = (Map<String, Object>) paths.get(path);
                trainingData.append(generatePathTrainingData(path, pathInfo));
            }
        }

        // Add business logic training data
        trainingData.append(generateBusinessLogicTrainingData());
        
        // Add common Q&A scenarios
        trainingData.append(generateCommonQAScenarios());

        return trainingData.toString();
    }

    @SuppressWarnings("unchecked")
    private String generatePathTrainingData(String path, Map<String, Object> pathInfo) {
        StringBuilder pathTraining = new StringBuilder();
        
        pathTraining.append("🔗 ").append(path).append("\n");
        
        for (String method : pathInfo.keySet()) {
            Map<String, Object> methodInfo = (Map<String, Object>) pathInfo.get(method);
            String summary = (String) methodInfo.get("summary");
            String description = (String) methodInfo.get("description");
            
            pathTraining.append("   ").append(method.toUpperCase()).append(": ");
            pathTraining.append(summary != null ? summary : "No description");
            if (description != null) {
                pathTraining.append(" - ").append(description);
            }
            pathTraining.append("\n");
        }
        pathTraining.append("\n");
        
        return pathTraining.toString();
    }

    private String generateBusinessLogicTrainingData() {
        return """
                
                💼 BUSINESS LOGIC TRAINING:
                ===========================
                
                🔐 AUTHENTICATION FLOW:
                Q: Quy trình đăng nhập như thế nào?
                A: 1. POST /v1/auth/login với username/password → Nhận JWT token
                   2. Sử dụng token trong header Authorization: Bearer <token>
                   3. Token có thể refresh qua POST /v1/auth/refresh
                   4. Đăng xuất qua POST /v1/auth/logout
                
                🛒 SHOPPING FLOW:
                Q: Quy trình mua hàng hoàn chỉnh?
                A: 1. Đăng ký/Đăng nhập → 2. Duyệt sản phẩm (GET /v1/products)
                   3. Thêm vào giỏ (POST /v1/carts/items) → 4. Đặt hàng (POST /v1/orders)
                   5. Thanh toán VNPay/COD → 6. Theo dõi đơn hàng (GET /v1/orders)
                
                💳 PAYMENT SYSTEM:
                Q: Hệ thống thanh toán hoạt động như thế nào?
                A: - VNPay: Tạo payment URL → Redirect → Callback xử lý kết quả
                   - COD: Đặt hàng với status Pending → Thanh toán khi nhận hàng
                   - Payment status: Pending → Paid/Cancelled
                
                📦 ORDER MANAGEMENT:
                Q: Các trạng thái đơn hàng?
                A: Pending (chờ xử lý) → Processing (đang xử lý) → Shipped (đã gửi) 
                   → Delivered (đã giao) hoặc Cancelled (đã hủy)
                
                🏷️ PRODUCT MANAGEMENT:
                Q: Cách tìm kiếm và lọc sản phẩm?
                A: - GET /v1/products?keyword=... (tìm kiếm)
                   - GET /v1/products?categoryId=... (lọc theo danh mục)
                   - Hỗ trợ pagination với page và size
                
                """;
    }

    private String generateCommonQAScenarios() {
        return """
                
                ❓ COMMON Q&A SCENARIOS:
                ========================
                
                Q: Tôi quên mật khẩu, làm sao lấy lại?
                A: Hiện tại hệ thống chưa có tính năng reset password tự động. Tôi sẽ chuyển câu hỏi của bạn đến nhân viên hỗ trợ ngay bây giờ.
                
                Q: Làm sao cập nhật thông tin cá nhân?
                A: Vào mục Profile, chỉnh sửa thông tin và lưu. API sử dụng: PUT /v1/users/{id}
                
                Q: Tôi muốn hủy đơn hàng đã đặt?
                A: Nếu đơn hàng còn ở trạng thái Pending/Processing, bạn có thể hủy qua API PUT /v1/orders/{id}/cancel. Nếu đã Shipped thì cần liên hệ admin.
                
                Q: Sản phẩm có bảo hành không?
                A: Thông tin bảo hành được ghi trong mô tả sản phẩm. Mỗi sản phẩm có chính sách bảo hành khác nhau.
                
                Q: Phí vận chuyển như thế nào?
                A: Phí vận chuyển phụ thuộc vào địa chỉ giao hàng và được tính trong tổng tiền đơn hàng.
                
                Q: Có thể đổi trả hàng không?
                A: Chính sách đổi trả theo quy định của shop. Vui lòng liên hệ admin để biết chi tiết cụ thể.
                
                Q: App có hỗ trợ notification không?
                A: Có, app ShopMate có hệ thống notification real-time qua WebSocket để thông báo về đơn hàng và tin nhắn.
                
                Q: Làm sao liên hệ với admin?
                A: Bạn có thể chat trực tiếp trong app, chọn "Chat với Admin" hoặc tôi có thể chuyển câu hỏi cho admin ngay.
                
                Q: API có rate limiting không?
                A: Hệ thống có giới hạn request để bảo vệ server. Nếu gặp lỗi 429, vui lòng chờ một chút rồi thử lại.
                
                Q: Dữ liệu có được bảo mật không?
                A: Tất cả API đều sử dụng HTTPS và JWT authentication. Thông tin cá nhân được mã hóa và bảo vệ theo tiêu chuẩn bảo mật.
                
                """;
    }

    /**
     * Test the AI with sample questions to validate training
     */
    public String testAIKnowledge() {
        List<String> testQuestions = List.of(
            "Làm sao để đăng ký tài khoản?",
            "Có những phương thức thanh toán nào?",
            "Làm sao theo dõi đơn hàng?",
            "Tôi muốn tìm sản phẩm theo danh mục?",
            "API authentication hoạt động như thế nào?"
        );

        StringBuilder testResults = new StringBuilder();
        testResults.append("🧪 AI KNOWLEDGE TEST RESULTS:\n");
        testResults.append("==============================\n\n");

        for (String question : testQuestions) {
            testResults.append("Q: ").append(question).append("\n");
            try {
                var response = enhancedGeminiService.getEnhancedResponseFromAI(question, "Testing AI knowledge");
                testResults.append("A: ").append(response.getReply()).append("\n");
                testResults.append("Need Human: ").append(response.isNeedHuman()).append("\n\n");
            } catch (Exception e) {
                testResults.append("A: Error testing - ").append(e.getMessage()).append("\n\n");
            }
        }

        return testResults.toString();
    }

    /**
     * Get API documentation summary for training
     */
    public String getAPIDocumentationSummary() {
        return """
                📖 FKITSHOP API DOCUMENTATION SUMMARY:
                =====================================
                
                Base URL: https://saleapp-mspd.onrender.com
                Swagger UI: https://saleapp-mspd.onrender.com/swagger-ui/index.html
                
                🔑 AUTHENTICATION:
                - All protected endpoints require JWT token
                - Header: Authorization: Bearer <token>
                - Login: POST /v1/auth/login
                - Register: POST /v1/auth/register
                
                📦 MAIN ENDPOINTS:
                - Products: /v1/products/**
                - Categories: /v1/categories/**
                - Cart: /v1/carts/**
                - Orders: /v1/orders/**
                - Payments: /v1/payments/**
                - Users: /v1/users/**
                - VNPay: /v1/vnpay/**
                - Chat: /v1/chat/**
                - Store Locations: /v1/store-locations/**
                
                📱 MOBILE INTEGRATION:
                - Android app "ShopMate"
                - Real-time WebSocket chat
                - VNPay payment gateway
                - JWT authentication
                
                This comprehensive API knowledge enables the AI to provide accurate,
                helpful assistance to customers using the FKitShop platform.
                """;
    }
}
