# 🤖 HƯỚNG DẪN TRAIN GEMINI AI CHO SHOPMATE - HOÀN CHỈNH

## ✅ BƯỚC 1-5: ĐÃ HOÀN THÀNH
- [x] SecurityConfig: Đã sửa conflict và thêm AI endpoints  
- [x] RestTemplate Bean: Đã thêm vào config
- [x] Application.yaml: Đã thêm swagger URL config
- [x] Compile: Project đã compile thành công
- [x] Conflict resolution: Đã sửa tất cả conflicts

## 🚀 BƯỚC 6: DEPLOY VÀ TEST

### 6.1 Deploy lên server của bạn
```bash
# Build project
mvn clean package -DskipTests

# Deploy jar file lên server 
# hoặc push code lên GitHub để auto-deploy
```

### 6.2 Test AI Training Endpoints
Sau khi deploy thành công, test các endpoints sau:

#### **Test 1: Fetch API Documentation**
```bash
curl -X GET "https://saleapp-mspd.onrender.com/v1/ai-training/api-docs" \
  -H "Content-Type: application/json"
```
**Kết quả mong đợi:** Trả về full API documentation từ Swagger

#### **Test 2: Build Context cho Product Search**
```bash
curl -X POST "https://saleapp-mspd.onrender.com/v1/ai-training/build-context" \
  -H "Content-Type: application/json" \
  -d '{"query": "Tôi muốn tìm sản phẩm laptop giá rẻ"}'
```
**Kết quả mong đợi:** Context về Product APIs và search functionality

#### **Test 3: Build Context cho Cart Management** 
```bash
curl -X POST "https://saleapp-mspd.onrender.com/v1/ai-training/build-context" \
  -H "Content-Type: application/json" \
  -d '{"query": "Làm sao thêm sản phẩm vào giỏ hàng?"}'
```
**Kết quả mong đợi:** Context về Cart APIs và authentication

#### **Test 4: Build Context cho Payment**
```bash
curl -X POST "https://saleapp-mspd.onrender.com/v1/ai-training/build-context" \
  -H "Content-Type: application/json" \
  -d '{"query": "Thanh toán VNPay như thế nào?"}'
```
**Kết quả mong đợi:** Context về VNPay APIs và payment flow

#### **Test 5: Get Training Scenarios**
```bash
curl -X GET "https://saleapp-mspd.onrender.com/v1/ai-training/scenarios"
```
**Kết quả mong đợi:** Common scenarios cho e-commerce

## 🧠 BƯỚC 7: TRAIN AI VỚI CONTEXT

### 7.1 Automatic Training
Khi user chat với AI, system sẽ **tự động**:
1. Build context dựa trên user query
2. Include relevant API information  
3. Enhance AI response quality

### 7.2 Test Enhanced AI Chat
```bash
# Test chat với AI (cần JWT token)
curl -X POST "https://saleapp-mspd.onrender.com/v1/chat-messages" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userID": YOUR_USER_ID,
    "receiverID": 23,
    "message": "Tôi muốn tìm laptop gaming giá dưới 20 triệu"
  }'
```

## 📊 BƯỚC 8: VERIFY TRAINING RESULTS

### Before Training:
```
User: "Làm sao tìm sản phẩm laptop?"
AI: "Bạn có thể tìm kiếm sản phẩm trên website."
```

### After Training:
```
User: "Làm sao tìm sản phẩm laptop?"
AI: "Bạn có thể tìm laptop bằng các cách sau:
1. Sử dụng tính năng search với từ khóa 'laptop'
2. Filter theo category 'Laptop/Computer'
3. Sắp xếp theo giá hoặc độ phổ biến
4. Xem sản phẩm best-seller trong danh mục laptop
Bạn có muốn tôi hướng dẫn chi tiết cách filter theo giá không?"
```

## 🔧 BƯỚC 9: MONITOR VÀ IMPROVE

### 9.1 Log Analysis
Monitor AI responses để identify:
- Câu hỏi AI trả lời chưa chính xác
- Missing context cho specific queries
- Cần thêm training data cho scenarios mới

### 9.2 Continuous Improvement
```bash
# Test context generation với different queries
curl -X POST "https://saleapp-mspd.onrender.com/v1/ai-training/test-context" \
  -H "Content-Type: application/json" \
  -d '{"query": "ORDER_QUERY_HERE"}'
```

## 🎯 KẾT QUẢ CUỐI CÙNG

Sau khi hoàn thành training, AI của bạn sẽ:

✅ **Hiểu rõ shop system** qua comprehensive API knowledge
✅ **Trả lời chính xác** về products, orders, payments, cart
✅ **Gợi ý thông minh** based on user intent
✅ **Handle complex scenarios** như payment issues, order tracking  
✅ **Provide step-by-step guidance** cho checkout process
✅ **Maintain consistency** với business rules

## 🚀 QUICK START COMMANDS

```bash
# Test full training system
chmod +x test_ai_training.sh
./test_ai_training.sh

# Hoặc test individual endpoints:
curl -X GET "https://saleapp-mspd.onrender.com/v1/ai-training/api-docs"
curl -X POST "https://saleapp-mspd.onrender.com/v1/ai-training/build-context" \
  -H "Content-Type: application/json" \
  -d '{"query": "YOUR_TEST_QUERY"}'
```

**🎉 CHÚC MỪNG! AI CỦA BẠN GIỜ ĐÃ LÀ MỘT SHOP ASSISTANT THÔNG MINH!**
