# 🤖 SMART AI SYSTEM - HƯỚNG DẪN SỬ DỤNG

## 🎯 TÍNH NĂNG MỚI: AI CÓ THỂ CALL API THỰC TẾ!

Smart AI system đã được tạo với khả năng:
✅ **Tự động call API** dựa trên user intent
✅ **Phân tích real-time data** từ database
✅ **Trả về thông tin chi tiết** về sản phẩm, giá cả, specs
✅ **Hiểu ngữ cảnh** và đưa ra recommendations

## 🚀 CÁCH TEST SMART AI

### **STEP 1: Start Server**
```bash
# Nếu chưa start
java -jar target/SalesApp-0.0.1-SNAPSHOT.jar

# Server sẽ chạy tại: http://localhost:8080
```

### **STEP 2: Test Smart AI Endpoints**

#### **Test Demo Endpoints (để xem example queries)**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/v1/smart-ai/demo" -Method GET
```

#### **Test Product Search Query**
```powershell
$body = '{"message": "Tôi muốn tìm laptop gaming giá dưới 30 triệu"}' 
Invoke-RestMethod -Uri "http://localhost:8080/v1/smart-ai/chat" -Method POST -Body $body -ContentType "application/json"
```

#### **Test Specific Product Query**  
```powershell
$body = '{"message": "Có MacBook nào không?"}' 
Invoke-RestMethod -Uri "http://localhost:8080/v1/smart-ai/chat" -Method POST -Body $body -ContentType "application/json"
```

#### **Test Category Search**
```powershell
$body = '{"message": "Samsung điện thoại nào tốt?"}' 
Invoke-RestMethod -Uri "http://localhost:8080/v1/smart-ai/chat" -Method POST -Body $body -ContentType "application/json"
```

#### **Test General Shop Inquiry**
```powershell
$body = '{"message": "Shop có bán gì?"}' 
Invoke-RestMethod -Uri "http://localhost:8080/v1/smart-ai/chat" -Method POST -Body $body -ContentType "application/json"
```

## 🧠 SỰ KHÁC BIỆT SO VỚI AI THƯỜNG

### **AI Thường (trước đây):**
```
User: "Tôi muốn mua laptop gaming"
AI: "Bạn có thể xem các sản phẩm laptop gaming trên website của chúng tôi"
```

### **Smart AI (hiện tại):**
```
User: "Tôi muốn mua laptop gaming"
AI sẽ:
1. 🔍 Call API GET /v1/products 
2. 🧮 Filter products có keyword "gaming" hoặc "laptop"
3. 📊 Phân tích specs, giá cả, stock
4. 💬 Trả về response chi tiết:

"Tôi đã tìm thấy các laptop gaming phù hợp với bạn:

**1. ASUS ROG Strix G15**
- Giá: 25,990,000 VND  
- CPU: AMD Ryzen 7 5800H
- GPU: RTX 3060 6GB
- RAM: 16GB DDR4
- Storage: 512GB SSD
- Còn hàng: 5 units

**2. MSI Gaming GF63**  
- Giá: 18,500,000 VND
- CPU: Intel i5-11400H
- GPU: GTX 1650 4GB  
- RAM: 8GB DDR4
- Storage: 256GB SSD
- Còn hàng: 8 units

**Recommendation:** 
- Nếu budget 30 triệu: ASUS ROG Strix G15 có performance tốt hơn
- Nếu muốn tiết kiệm: MSI GF63 vẫn chơi game tốt ở setting medium

Bạn có muốn xem thêm thông tin về model nào không?"
```

## 🎮 DEMO SCENARIOS

### **Scenario 1: Product Search**
```json
{
  "message": "Tôi cần laptop cho học lập trình"
}
```
**Expected:** AI sẽ call API, filter laptop phù hợp programming, explain specs

### **Scenario 2: Price Range Query**  
```json
{
  "message": "iPhone nào dưới 20 triệu?"
}
```
**Expected:** AI sẽ filter iPhone theo price range, so sánh models

### **Scenario 3: Brand Specific**
```json
{
  "message": "Samsung Galaxy mới nhất là gì?"
}
```
**Expected:** AI sẽ tìm products Samsung, sort by latest, explain features

### **Scenario 4: Technical Specs**
```json
{
  "message": "Laptop nào có RAM 16GB?"
}
```
**Expected:** AI sẽ filter theo specs, explain performance benefits

## 🔧 HOW IT WORKS

### **1. Intent Detection**
```java
if (message.contains("laptop") || message.contains("tìm sản phẩm")) {
    intent = "PRODUCT_SEARCH";
}
```

### **2. API Calling**
```java
// Call real API
GET http://localhost:8080/v1/products
// Parse JSON response
// Filter based on user criteria
```

### **3. Data Analysis**
```java
// Extract relevant products
// Format product information  
// Build context for AI
```

### **4. Enhanced Response**
```java
// Send to Gemini với real data context
// Get intelligent response
// Return formatted answer
```

## 🚀 NEXT STEPS

### **1. Test với Chat Integration**
Khi bạn chat với AI qua mobile app, AI sẽ tự động sử dụng Smart AI system!

### **2. Expand API Coverage** 
- Cart API integration (cần auth)
- Order API integration (cần auth)
- Payment status queries

### **3. Advanced Features**
- Product comparison
- Price alerts
- Inventory notifications
- Personalized recommendations

## ✨ KẾT QUẢ

Smart AI giờ đây có thể:
🎯 **Hiểu chính xác** user muốn gì
🔍 **Tìm kiếm real-time** trong database  
📊 **Phân tích và so sánh** products
💬 **Giải thích chi tiết** specs và features
🎯 **Đưa ra recommendations** phù hợp

**🎉 AI của bạn giờ đây là một SALES EXPERT thực sự!**
