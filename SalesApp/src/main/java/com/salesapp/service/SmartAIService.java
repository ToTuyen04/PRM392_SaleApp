package com.salesapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesapp.entity.Gemini;
import com.salesapp.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmartAIService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final GeminiService geminiService;
    private final ProductService productService;
    
    @Value("${app.server.host:localhost}")
    private String serverHost;
    
    @Value("${app.server.port:8080}")
    private String serverPort;

    /**
     * Enhanced AI response với khả năng call API thực tế
     */
    public Gemini getSmartResponse(String userMessage) {
        try {
            // Phân tích user intent
            String intent = analyzeUserIntent(userMessage);
            log.info("Detected user intent: {}", intent);
            
            // Call API dựa trên intent và get real data
            String apiData = callRelevantAPI(intent, userMessage);
            
            // Build context với real data
            String enhancedContext = buildContextWithRealData(intent, apiData, userMessage);
            
            // Get AI response với enhanced context
            return geminiService.getResponseFromAIWithContext(userMessage, enhancedContext);
            
        } catch (Exception e) {
            log.error("Error in smart AI response: ", e);
            return geminiService.getResponseFromAI(userMessage);
        }
    }

    /**
     * Phân tích user intent để xác định cần call API nào
     */
    private String analyzeUserIntent(String userMessage) {
        String message = userMessage.toLowerCase();
        
        if (message.contains("tìm") || message.contains("search") || 
            message.contains("laptop") || message.contains("điện thoại") || 
            message.contains("sản phẩm")) {
            return "PRODUCT_SEARCH";
        }
        
        if (message.contains("giỏ hàng") || message.contains("cart")) {
            return "CART_INFO";
        }
        
        if (message.contains("đơn hàng") || message.contains("order")) {
            return "ORDER_INFO";
        }
        
        if (message.contains("thanh toán") || message.contains("payment")) {
            return "PAYMENT_INFO";
        }
        
        return "GENERAL";
    }

    /**
     * Call API thực tế dựa trên intent
     */
    private String callRelevantAPI(String intent, String userMessage) {
        String baseUrl = "http://" + serverHost + ":" + serverPort;
        
        try {
            switch (intent) {
                case "PRODUCT_SEARCH":
                    return callProductAPI(baseUrl, userMessage);
                case "CART_INFO":
                    return callCartAPI(baseUrl);
                case "ORDER_INFO":
                    return callOrderAPI(baseUrl);
                case "PAYMENT_INFO":
                    return callPaymentAPI(baseUrl);
                default:
                    return "No specific API data needed";
            }
        } catch (Exception e) {
            log.error("Error calling API for intent {}: ", intent, e);
            return "API call failed: " + e.getMessage();
        }
    }

    /**
     * Call Product API và filter results - FIXED: Direct service call
     */
    private String callProductAPI(String baseUrl, String userMessage) {
        try {
            log.info("Getting products directly from ProductService (avoiding HTTP loop)");
            
            // Get products directly từ service - sử dụng method có sẵn
            List<ProductResponse> allProducts = productService.getAllProducts();
            
            if (allProducts == null || allProducts.isEmpty()) {
                return "No products found in database";
            }
            
            // Filter products based on user query
            String searchKeyword = extractSearchKeyword(userMessage);
            StringBuilder result = new StringBuilder();
            result.append("PRODUCTS_DATA:\n");
            
            int count = 0;
            for (ProductResponse product : allProducts) {
                String productName = product.getProductName() != null ? product.getProductName() : "";
                String description = product.getBriefDescription() != null ? product.getBriefDescription() : "";
                
                // Check if product matches search keyword
                if (containsKeyword(productName, searchKeyword) || 
                    containsKeyword(description, searchKeyword)) {
                    
                    result.append(formatProductInfoDirect(product));
                    count++;
                    
                    if (count >= 5) break; // Limit to 5 products
                }
            }
            
            if (count == 0) {
                result.append("No products found matching: ").append(searchKeyword);
            }
            
            log.info("Found {} products matching keyword: {}", count, searchKeyword);
            return result.toString();
            
        } catch (Exception e) {
            log.error("Error calling product API: ", e);
            return "Error fetching product data: " + e.getMessage();
        }
    }

    /**
     * Extract search keyword từ user message
     */
    private String extractSearchKeyword(String userMessage) {
        String message = userMessage.toLowerCase();
        
        // Common product keywords
        if (message.contains("laptop")) return "laptop";
        if (message.contains("điện thoại") || message.contains("phone")) return "phone";
        if (message.contains("iphone")) return "iphone";
        if (message.contains("samsung")) return "samsung";
        if (message.contains("macbook")) return "macbook";
        if (message.contains("gaming")) return "gaming";
        
        // Extract any word that might be product name
        String[] words = message.split("\\s+");
        for (String word : words) {
            if (word.length() > 3 && !isCommonWord(word)) {
                return word;
            }
        }
        
        return ""; // No specific keyword found
    }

    private boolean isCommonWord(String word) {
        String[] commonWords = {"muốn", "tìm", "mua", "kiếm", "sản", "phẩm", "giá", "rẻ", "tốt", "nhất"};
        for (String common : commonWords) {
            if (word.contains(common)) return true;
        }
        return false;
    }

    private boolean containsKeyword(String text, String keyword) {
        if (keyword.isEmpty()) return false;
        return text.toLowerCase().contains(keyword.toLowerCase());
    }

    /**
     * Format product information cho AI
     */
    private String formatProductInfo(JsonNode product) {
        StringBuilder info = new StringBuilder();
        
        info.append("- Product: ").append(product.get("productName").asText("N/A")).append("\n");
        info.append("  Price: ").append(formatPrice(product.get("price").asDouble(0))).append("\n");
        info.append("  Description: ").append(product.get("description").asText("N/A")).append("\n");
        info.append("  Stock: ").append(product.get("stockQuantity").asInt(0)).append(" units\n");
        
        if (product.has("categoryID") && product.get("categoryID").has("categoryName")) {
            info.append("  Category: ").append(product.get("categoryID").get("categoryName").asText("N/A")).append("\n");
        }
        
        info.append("\n");
        
        return info.toString();
    }

    /**
     * Format ProductResponse cho AI - FIXED: Direct method
     */
    private String formatProductInfoDirect(ProductResponse product) {
        StringBuilder info = new StringBuilder();
        
        info.append("- Product: ").append(product.getProductName() != null ? product.getProductName() : "N/A").append("\n");
        info.append("  Price: ").append(formatPrice(product.getPrice() != null ? product.getPrice().doubleValue() : 0)).append("\n");
        info.append("  Brief Description: ").append(product.getBriefDescription() != null ? product.getBriefDescription() : "N/A").append("\n");
        
        if (product.getFullDescription() != null && !product.getFullDescription().isEmpty()) {
            info.append("  Full Description: ").append(product.getFullDescription()).append("\n");
        }
        
        if (product.getTechnicalSpecifications() != null && !product.getTechnicalSpecifications().isEmpty()) {
            info.append("  Technical Specs: ").append(product.getTechnicalSpecifications()).append("\n");
        }
        
        if (product.getCategoryID() != null) {
            info.append("  Category ID: ").append(product.getCategoryID()).append("\n");
        }
        
        info.append("\n");
        
        return info.toString();
    }

    private String formatPrice(double price) {
        return String.format("%,.0f VND", price);
    }

    /**
     * Call Cart API (cần authentication - simplified version)
     */
    private String callCartAPI(String baseUrl) {
        return "CART_INFO: Cart API requires authentication. Please login first.";
    }

    /**
     * Call Order API (cần authentication - simplified version)
     */
    private String callOrderAPI(String baseUrl) {
        return "ORDER_INFO: Order API requires authentication. Please login first.";
    }

    /**
     * Call Payment API (cần authentication - simplified version)  
     */
    private String callPaymentAPI(String baseUrl) {
        return "PAYMENT_INFO: Payment information requires authentication. Please login first.";
    }

    /**
     * Build enhanced context với real data
     */
    private String buildContextWithRealData(String intent, String apiData, String userMessage) {
        StringBuilder context = new StringBuilder();
        
        context.append("=== SHOP ASSISTANT WITH REAL-TIME DATA ===\n");
        context.append("User Intent: ").append(intent).append("\n");
        context.append("User Query: ").append(userMessage).append("\n\n");
        
        context.append("=== REAL-TIME API DATA ===\n");
        context.append(apiData).append("\n\n");
        
        context.append("=== INSTRUCTIONS FOR AI ===\n");
        context.append("Based on the real-time data above, provide a helpful and detailed response.\n");
        context.append("If products are found:\n");
        context.append("- Explain each product clearly\n");
        context.append("- Mention key features, specs, and price\n");
        context.append("- Make recommendations based on user needs\n");
        context.append("- Suggest related products or alternatives\n");
        context.append("- Be conversational and helpful\n\n");
        
        if (intent.equals("PRODUCT_SEARCH")) {
            context.append("For product recommendations:\n");
            context.append("- Focus on the products that match user criteria\n");
            context.append("- Explain why each product is suitable\n");
            context.append("- Mention any special features or advantages\n");
            context.append("- Suggest best value options\n");
        }
        
        return context.toString();
    }
}
