package com.salesapp.controller.v1;

import com.salesapp.dto.response.ResponseObject;
import com.salesapp.entity.Gemini;
import com.salesapp.service.SmartAIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/smart-ai")
@RequiredArgsConstructor
@Tag(name = "Smart AI", description = "AI with Real-time API Calling Capability")
public class SmartAIV1Controller {

    private final SmartAIService smartAIService;

    @PostMapping("/chat")
    @Operation(summary = "Chat with Smart AI", 
               description = "AI will call real APIs and analyze data to provide intelligent responses")
    public ResponseObject<String> chatWithSmartAI(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return ResponseObject.<String>builder()
                    .status(9001)
                    .message("Message cannot be empty")
                    .build();
        }

        try {
            Gemini response = smartAIService.getSmartResponse(userMessage);
            
            return ResponseObject.<String>builder()
                    .status(1000)
                    .message("Smart AI response generated successfully")
                    .data(response.getReply())
                    .build();
                    
        } catch (Exception e) {
            return ResponseObject.<String>builder()
                    .status(9999)
                    .message("Error generating AI response: " + e.getMessage())
                    .build();
        }
    }

    @PostMapping("/demo")
    @Operation(summary = "Demo Smart AI capabilities", 
               description = "Test different types of queries to see AI calling APIs")
    public ResponseObject<Map<String, Object>> demoSmartAI() {
        Map<String, Object> demos = Map.of(
            "product_search", "Tôi muốn tìm laptop gaming giá dưới 30 triệu",
            "specific_product", "Có MacBook nào không?",
            "category_search", "Sản phẩm điện thoại Samsung nào tốt?",
            "general_inquiry", "Shop có bán gì?",
            "cart_question", "Giỏ hàng của tôi có gì?",
            "order_question", "Đơn hàng của tôi đang ở đâu?"
        );

        return ResponseObject.<Map<String, Object>>builder()
                .status(1000)
                .message("Demo queries for Smart AI")
                .data(demos)
                .build();
    }
}
