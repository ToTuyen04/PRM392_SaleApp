package com.salesapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiTrainingService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${app.swagger.url:https://saleapp-mspd.onrender.com}")
    private String baseUrl;

    /**
     * Fetch API documentation từ Swagger
     */
    public String fetchApiDocumentation() {
        try {
            String swaggerUrl = baseUrl + "/v3/api-docs";
            log.info("Fetching API documentation from: {}", swaggerUrl);
            
            String apiDocs = restTemplate.getForObject(swaggerUrl, String.class);
            return parseSwaggerToTrainingData(apiDocs);
            
        } catch (Exception e) {
            log.error("Error fetching API documentation: ", e);
            return getFallbackApiDocumentation();
        }
    }

    /**
     * Parse Swagger JSON thành training data
     */
    private String parseSwaggerToTrainingData(String swaggerJson) {
        try {
            JsonNode root = objectMapper.readTree(swaggerJson);
            StringBuilder trainingData = new StringBuilder();
            
            trainingData.append("# ShopMate E-commerce API Documentation\n\n");
            
            // Parse paths
            JsonNode paths = root.get("paths");
            if (paths != null) {
                paths.fields().forEachRemaining(entry -> {
                    String path = entry.getKey();
                    JsonNode methods = entry.getValue();
                    
                    methods.fields().forEachRemaining(methodEntry -> {
                        String method = methodEntry.getKey().toUpperCase();
                        JsonNode operation = methodEntry.getValue();
                        
                        trainingData.append("## ").append(method).append(" ").append(path).append("\n");
                        
                        // Description
                        if (operation.has("summary")) {
                            trainingData.append("**Mô tả:** ").append(operation.get("summary").asText()).append("\n");
                        }
                        
                        // Parameters
                        if (operation.has("parameters")) {
                            trainingData.append("**Parameters:**\n");
                            operation.get("parameters").forEach(param -> {
                                String name = param.get("name").asText();
                                String type = param.has("schema") ? param.get("schema").get("type").asText() : "string";
                                String location = param.get("in").asText();
                                trainingData.append("- ").append(name).append(" (").append(type).append(", ").append(location).append(")\n");
                            });
                        }
                        
                        // Request body
                        if (operation.has("requestBody")) {
                            trainingData.append("**Request Body:** JSON object\n");
                        }
                        
                        // Tags (category)
                        if (operation.has("tags")) {
                            trainingData.append("**Category:** ").append(operation.get("tags").get(0).asText()).append("\n");
                        }
                        
                        trainingData.append("\n");
                    });
                });
            }
            
            return trainingData.toString();
            
        } catch (Exception e) {
            log.error("Error parsing swagger JSON: ", e);
            return getFallbackApiDocumentation();
        }
    }

    /**
     * Build context cho specific user query
     */
    public String buildShopContext(String userQuery) {
        StringBuilder context = new StringBuilder();
        
        // System context
        context.append("Bạn là AI assistant chuyên về hệ thống E-commerce ShopMate. ");
        context.append("Dưới đây là thông tin về các API và tính năng của shop:\n\n");
        
        String query = userQuery.toLowerCase();
        
        // Product context
        if (query.contains("sản phẩm") || query.contains("product") || 
            query.contains("tìm") || query.contains("search")) {
            context.append(getProductApiContext());
        }
        
        // Cart context  
        if (query.contains("giỏ hàng") || query.contains("cart") || 
            query.contains("thêm") || query.contains("add")) {
            context.append(getCartApiContext());
        }
        
        // Order context
        if (query.contains("đặt hàng") || query.contains("order") || 
            query.contains("đơn hàng") || query.contains("mua")) {
            context.append(getOrderApiContext());
        }
        
        // Payment context
        if (query.contains("thanh toán") || query.contains("payment") || 
            query.contains("vnpay") || query.contains("tiền")) {
            context.append(getPaymentApiContext());
        }
        
        // User context
        if (query.contains("đăng nhập") || query.contains("login") || 
            query.contains("tài khoản") || query.contains("profile")) {
            context.append(getUserApiContext());
        }
        
        // General business flow
        context.append(getBusinessFlowContext());
        
        return context.toString();
    }

    private String getProductApiContext() {
        return """
            ## SẢN PHẨM (PRODUCTS):
            - GET /v1/products: Lấy tất cả sản phẩm
            - GET /v1/products/{id}: Chi tiết sản phẩm
            - GET /v1/products/category/{categoryId}: Sản phẩm theo danh mục
            - GET /v1/products/search: Tìm kiếm với filter (productName, category, priceRange, sortBy)
            - GET /v1/products/filter-options: Lấy options cho filter
            - GET /v1/products/stats/most-ordered: Sản phẩm bán chạy
            
            """;
    }

    private String getCartApiContext() {
        return """
            ## GIỎ HÀNG (CART):
            - GET /v1/carts: Xem giỏ hàng (cần đăng nhập)
            - POST /v1/carts/items: Thêm sản phẩm vào giỏ {productId, quantity}
            - PUT /v1/carts/items/{itemId}: Cập nhật số lượng {quantity}
            - DELETE /v1/carts/items/{itemId}: Xóa khỏi giỏ hàng
            
            """;
    }

    private String getOrderApiContext() {
        return """
            ## ĐẶT HÀNG (ORDERS):
            - POST /v1/orders: Tạo đơn hàng từ cart {shippingAddress, paymentMethod, notes}
            - GET /v1/orders: Lịch sử đơn hàng của user
            - GET /v1/orders/{id}: Chi tiết đơn hàng cụ thể
            
            """;
    }

    private String getPaymentApiContext() {
        return """
            ## THANH TOÁN (PAYMENT):
            - GET /v1/payments: Danh sách payments
            - PUT /v1/payments/{id}/status: Cập nhật trạng thái {paymentStatus: Paid|Cancelled|Pending}
            - POST /v1/vnpay/create-payment: Tạo link thanh toán VNPay {orderId, amount, returnUrl}
            - GET /v1/vnpay/payment-callback: Xử lý kết quả từ VNPay
            
            """;
    }

    private String getUserApiContext() {
        return """
            ## TÀI KHOẢN (USER/AUTH):
            - POST /v1/auth/register: Đăng ký {email, password, fullName, phone}
            - POST /v1/auth/login: Đăng nhập {email, password}
            - POST /v1/auth/refresh: Refresh token
            - GET /v1/users/profile: Xem profile (cần đăng nhập)
            - PUT /v1/users/profile: Cập nhật profile
            
            """;
    }

    private String getBusinessFlowContext() {
        return """
            ## QUY TRÌNH MUA HÀNG:
            1. Tìm sản phẩm → Search/Browse products
            2. Thêm vào giỏ → Add to cart
            3. Xem giỏ hàng → View cart
            4. Đặt hàng → Create order
            5. Thanh toán → VNPay payment
            6. Theo dõi đơn → Track order
            
            ## LƯU Ý:
            - Cần đăng nhập để sử dụng cart, order, payment
            - VNPay hỗ trợ thanh toán online
            - Có thể filter sản phẩm theo category, giá, tên
            - Payment status: Paid (đã thanh toán), Pending (chờ), Cancelled (hủy)
            
            """;
    }

    /**
     * Fallback documentation khi không fetch được từ swagger
     */
    private String getFallbackApiDocumentation() {
        return """
            # ShopMate E-commerce API (Fallback Documentation)
            
            ## Core APIs:
            - Products: /v1/products (GET, POST, PUT, DELETE)
            - Categories: /v1/categories
            - Cart: /v1/carts, /v1/carts/items
            - Orders: /v1/orders
            - Payments: /v1/payments, /v1/vnpay
            - Auth: /v1/auth (login, register, refresh)
            - Users: /v1/users
            
            """;
    }

    /**
     * Training data cho common scenarios
     */
    public Map<String, String> getCommonScenarioTraining() {
        Map<String, String> scenarios = new HashMap<>();
        
        scenarios.put("search_product", 
            "User muốn tìm sản phẩm → Sử dụng GET /v1/products/search với params productName, category, priceRange");
            
        scenarios.put("add_to_cart", 
            "User muốn thêm sản phẩm vào giỏ → POST /v1/carts/items với {productId, quantity}");
            
        scenarios.put("checkout", 
            "User muốn thanh toán → POST /v1/orders để tạo đơn, sau đó POST /v1/vnpay/create-payment");
            
        scenarios.put("check_order", 
            "User muốn kiểm tra đơn hàng → GET /v1/orders/{orderId} để xem chi tiết và trạng thái");
            
        scenarios.put("payment_status", 
            "User hỏi về thanh toán → Kiểm tra payment status trong order details: Paid/Pending/Cancelled");
            
        return scenarios;
    }
}
