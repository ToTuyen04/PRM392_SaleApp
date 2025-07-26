# Hướng dẫn Train Gemini AI cho ShopMate

## 🎯 Mục tiêu hoàn thành

Tôi đã tạo một hệ thống training comprehensive cho Gemini AI để hiểu toàn bộ shop của bạn thông qua API documentation. Hệ thống này sẽ giúp AI trả lời các câu hỏi về shop một cách thông minh và chính xác.

## 🛠️ Những gì đã được tạo

### 1. **GeminiTrainingService** 
📍 `src/main/java/com/salesapp/service/GeminiTrainingService.java`

**Chức năng:**
- Tự động fetch API documentation từ Swagger (`https://saleapp-mspd.onrender.com/v3/api-docs`)
- Parse và chuyển đổi thành training data
- Build context thông minh dựa trên user query
- Cung cấp training scenarios cho các use case phổ biến

**Key Methods:**
```java
// Fetch API docs tự động
String fetchApiDocumentation()

// Build context dựa trên query
String buildShopContext(String userQuery)

// Get training scenarios
Map<String, String> getCommonScenarioTraining()
```

### 2. **AITrainingV1Controller**
📍 `src/main/java/com/salesapp/controller/v1/AITrainingV1Controller.java`

**Endpoints:**
- `GET /v1/ai-training/api-docs` - Lấy API documentation
- `POST /v1/ai-training/build-context` - Build context cho query
- `GET /v1/ai-training/scenarios` - Lấy common scenarios
- `POST /v1/ai-training/test-context` - Test context generation

### 3. **Enhanced ChatMessageService**
📍 `src/main/java/com/salesapp/service/ChatMessageService.java`

**Cập nhật:**
- Tích hợp `GeminiTrainingService`
- Tự động build context khi user chat với AI
- Enhanced AI responses với shop knowledge

### 4. **Enhanced GeminiService**
📍 `src/main/java/com/salesapp/service/GeminiService.java`

**Method mới:**
```java
// AI response với context
Gemini getResponseFromAIWithContext(String userMessage, String additionalContext)
```

### 5. **Comprehensive Documentation**
📍 `GEMINI_AI_TRAINING_GUIDE.md`

Hướng dẫn chi tiết về:
- API structure training
- Business logic training  
- Error handling
- Training prompts
- Test cases

## 🚀 Cách sử dụng

### Bước 1: Test Training System
```bash
# Fetch API documentation
curl -X GET "https://saleapp-mspd.onrender.com/v1/ai-training/api-docs"

# Test context building
curl -X POST "https://saleapp-mspd.onrender.com/v1/ai-training/build-context" \
  -H "Content-Type: application/json" \
  -d '{"query": "Tôi muốn tìm sản phẩm laptop"}'
```

### Bước 2: Chat với Enhanced AI
Khi user chat với AI, system sẽ:
1. Automatically build context based on user query
2. Include relevant API information
3. Provide intelligent responses about shop features

### Bước 3: Monitor và Improve
```bash
# Test different query types
curl -X POST "https://saleapp-mspd.onrender.com/v1/ai-training/test-context" \
  -H "Content-Type: application/json" \
  -d '{"query": "Đơn hàng của tôi thanh toán chưa?"}'
```

## 🧠 AI Training Process

### Context Building Logic
System tự động detect loại query và build relevant context:

**Product Queries** → Product APIs + Search functionality
- "tìm sản phẩm", "laptop", "điện thoại"
- Context: `/v1/products/*` APIs

**Cart Queries** → Cart management + Authentication  
- "giỏ hàng", "thêm vào cart"
- Context: `/v1/carts/*` APIs

**Order Queries** → Order management + Tracking
- "đặt hàng", "đơn hàng", "order"
- Context: `/v1/orders/*` APIs

**Payment Queries** → VNPay + Payment status
- "thanh toán", "VNPay", "payment"
- Context: `/v1/payments/*`, `/v1/vnpay/*` APIs

### Enhanced System Prompt
AI được training với:
- Full API documentation
- Business rules và workflows
- Error handling scenarios
- Common customer questions

## 📊 Example Training Results

### Before Training:
```
User: "Làm sao tìm sản phẩm laptop?"
AI: "Bạn có thể tìm kiếm sản phẩm trên website."
```

### After Training:
```
User: "Làm sao tìm sản phẩm laptop?"
AI: "Bạn có thể tìm laptop bằng các cách sau:
1. Sử dụng chức năng search với từ khóa 'laptop'
2. Filter theo category 'Laptop/Computer' 
3. Sắp xếp theo giá hoặc độ phổ biến
4. Xem sản phẩm best-seller trong danh mục laptop

Bạn có muốn tôi hướng dẫn chi tiết cách filter theo giá không?"
```

## 🔧 Configuration

### SecurityConfig Update
```java
private final String[] PUBLIC_ENDPOINTS = {
    // ... existing endpoints
    "/v*/ai-training/**"  // ✅ Added
};
```

### Application Properties
```yaml
app:
  swagger:
    url: https://saleapp-mspd.onrender.com
  gemini:
    api:
      url: ${GEMINI_API_URL}
      key: ${GEMINI_API_KEY}
```

## 🧪 Testing Scenarios

### 1. Product Search
```json
{
  "query": "Tìm sản phẩm iPhone 15 giá tốt"
}
```
**Expected Context:** Product APIs, search parameters, price filtering

### 2. Cart Management  
```json
{
  "query": "Thêm sản phẩm vào giỏ hàng bị lỗi"
}
```
**Expected Context:** Cart APIs, authentication, error handling

### 3. Order Tracking
```json
{
  "query": "Kiểm tra đơn hàng #12345"
}
```
**Expected Context:** Order APIs, tracking, payment status

### 4. Payment Issues
```json
{
  "query": "VNPay không thanh toán được"
}
```
**Expected Context:** VNPay APIs, payment flow, troubleshooting

## 📈 Benefits

### ✅ Intelligent Context
- AI hiểu đúng ngữ cảnh câu hỏi
- Provide relevant API information
- Suggest related features

### ✅ Comprehensive Knowledge
- Full understanding của shop system
- Business rules và workflows
- Error handling guidance

### ✅ Automatic Updates
- Auto-fetch latest API changes
- Dynamic context building
- Continuous learning

### ✅ Better User Experience
- Accurate answers
- Relevant suggestions  
- Professional responses

## 🔄 Continuous Improvement

### Auto-Update Training (Future)
```java
@Scheduled(fixedRate = 24 * 60 * 60 * 1000) // Daily
public void updateTrainingData() {
    // Fetch latest API changes
    // Update context templates
    // Retrain common scenarios
}
```

### Feedback Integration
```java
public void processUserFeedback(String query, String response, boolean helpful) {
    if (!helpful) {
        // Log for improvement
        // Update training data
        // Enhance context building
    }
}
```

## ✨ Kết quả

Với hệ thống training này, Gemini AI của bạn sẽ:

1. **Hiểu rõ shop system** thông qua comprehensive API knowledge
2. **Trả lời chính xác** các câu hỏi về products, orders, payments
3. **Gợi ý thông minh** based on user intent và available features  
4. **Handle complex scenarios** like payment issues, order tracking
5. **Provide step-by-step guidance** cho các processes như checkout
6. **Maintain consistency** với business rules và policies

🎉 **AI của bạn giờ đây là một shop assistant thực sự thông minh!**
