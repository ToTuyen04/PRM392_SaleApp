package com.salesapp.controller.v1;

import com.salesapp.dto.response.ResponseObject;
import com.salesapp.service.GeminiTrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/ai-training")
@RequiredArgsConstructor
@Tag(name = "AI Training", description = "Gemini AI Training and Context Management")
public class AITrainingV1Controller {

    private final GeminiTrainingService geminiTrainingService;

    @GetMapping("/api-docs")
    @Operation(summary = "Fetch API documentation for training", 
               description = "Get structured API documentation from Swagger for AI training")
    public ResponseObject<String> getApiDocumentation() {
        String apiDocs = geminiTrainingService.fetchApiDocumentation();
        return ResponseObject.<String>builder()
                .status(1000)
                .message("API documentation fetched successfully")
                .data(apiDocs)
                .build();
    }

    @PostMapping("/build-context")
    @Operation(summary = "Build context for user query", 
               description = "Generate relevant context based on user query for better AI responses")
    public ResponseObject<String> buildContext(@RequestBody Map<String, String> request) {
        String userQuery = request.get("query");
        if (userQuery == null || userQuery.trim().isEmpty()) {
            return ResponseObject.<String>builder()
                    .status(9001)
                    .message("Query cannot be empty")
                    .build();
        }

        String context = geminiTrainingService.buildShopContext(userQuery);
        return ResponseObject.<String>builder()
                .status(1000)
                .message("Context built successfully")
                .data(context)
                .build();
    }

    @GetMapping("/scenarios")
    @Operation(summary = "Get common scenario training data", 
               description = "Retrieve pre-defined training scenarios for common e-commerce use cases")
    public ResponseObject<Map<String, String>> getCommonScenarios() {
        Map<String, String> scenarios = geminiTrainingService.getCommonScenarioTraining();
        return ResponseObject.<Map<String, String>>builder()
                .status(1000)
                .message("Common scenarios retrieved successfully")
                .data(scenarios)
                .build();
    }

    @PostMapping("/test-context")
    @Operation(summary = "Test AI context generation", 
               description = "Test how context is built for different types of queries")
    public ResponseObject<Map<String, Object>> testContext(@RequestBody Map<String, String> request) {
        String userQuery = request.get("query");
        if (userQuery == null || userQuery.trim().isEmpty()) {
            return ResponseObject.<Map<String, Object>>builder()
                    .status(9001)
                    .message("Query cannot be empty")
                    .build();
        }

        String context = geminiTrainingService.buildShopContext(userQuery);
        
        Map<String, Object> result = Map.of(
            "originalQuery", userQuery,
            "generatedContext", context,
            "contextLength", context.length(),
            "recommendation", getQueryTypeRecommendation(userQuery)
        );

        return ResponseObject.<Map<String, Object>>builder()
                .status(1000)
                .message("Context test completed")
                .data(result)
                .build();
    }

    private String getQueryTypeRecommendation(String query) {
        String lowerQuery = query.toLowerCase();
        
        if (lowerQuery.contains("sản phẩm") || lowerQuery.contains("tìm")) {
            return "Product search query - Context includes product APIs and search functionality";
        }
        if (lowerQuery.contains("giỏ hàng") || lowerQuery.contains("cart")) {
            return "Cart management query - Context includes cart operations and authentication";
        }
        if (lowerQuery.contains("đặt hàng") || lowerQuery.contains("order")) {
            return "Order management query - Context includes order creation and tracking";
        }
        if (lowerQuery.contains("thanh toán") || lowerQuery.contains("payment")) {
            return "Payment query - Context includes VNPay integration and payment status";
        }
        if (lowerQuery.contains("đăng nhập") || lowerQuery.contains("tài khoản")) {
            return "User authentication query - Context includes auth APIs and user management";
        }
        
        return "General query - Context includes business flow and common scenarios";
    }
}
