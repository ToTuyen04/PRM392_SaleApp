# Gemini AI Training Guide for ShopMate E-commerce

## Mục tiêu
Train Gemini AI để hiểu toàn bộ hệ thống ShopMate E-commerce thông qua API documentation và có thể trả lời mọi câu hỏi về shop một cách thông minh.

## 1. API Documentation Training

### 1.1 Product Management APIs
```json
{
  "category": "Product Management",
  "endpoints": [
    {
      "method": "GET",
      "path": "/v1/products",
      "description": "Lấy danh sách tất cả sản phẩm",
      "response": "Trả về danh sách sản phẩm với thông tin: id, tên, giá, mô tả, hình ảnh, category"
    },
    {
      "method": "GET", 
      "path": "/v1/products/{id}",
      "description": "Lấy chi tiết sản phẩm theo ID",
      "parameters": ["productId: int"],
      "response": "Thông tin chi tiết sản phẩm bao gồm specs, reviews, availability"
    },
    {
      "method": "GET",
      "path": "/v1/products/category/{categoryId}",
      "description": "Lấy sản phẩm theo danh mục",
      "parameters": ["categoryId: int"],
      "use_case": "Khi khách hàng muốn xem sản phẩm trong một category cụ thể"
    },
    {
      "method": "GET",
      "path": "/v1/products/search",
      "description": "Tìm kiếm sản phẩm với filters",
      "parameters": [
        "productName: string (tên sản phẩm)",
        "category: string (danh mục)",
        "priceRange: string (khoảng giá)",
        "sortBy: string (sắp xếp theo)",
        "page: int (trang hiện tại)",
        "size: int (số lượng per page)"
      ],
      "use_case": "Tìm kiếm sản phẩm với nhiều bộ lọc"
    },
    {
      "method": "GET",
      "path": "/v1/products/filter-options",
      "description": "Lấy tất cả options cho filter",
      "response": "Danh sách categories, price ranges, sort options"
    },
    {
      "method": "GET",
      "path": "/v1/products/stats/most-ordered",
      "description": "Sản phẩm được đặt nhiều nhất",
      "parameters": ["limit: int"],
      "use_case": "Hiển thị sản phẩm best-seller"
    }
  ]
}
```

### 1.2 Category Management APIs
```json
{
  "category": "Category Management",
  "endpoints": [
    {
      "method": "GET",
      "path": "/v1/categories",
      "description": "Lấy danh sách tất cả categories",
      "response": "Danh sách categories với id, name, description, productCount"
    },
    {
      "method": "GET",
      "path": "/v1/categories/{id}",
      "description": "Chi tiết category theo ID",
      "response": "Thông tin category và danh sách sản phẩm trong category"
    }
  ]
}
```

### 1.3 Cart & Order Management APIs
```json
{
  "category": "Cart & Order Management",
  "endpoints": [
    {
      "method": "GET",
      "path": "/v1/carts",
      "description": "Lấy giỏ hàng của user hiện tại",
      "authentication": "Required JWT token",
      "response": "Danh sách items trong cart với quantity, price, total"
    },
    {
      "method": "POST",
      "path": "/v1/carts/items",
      "description": "Thêm sản phẩm vào giỏ hàng",
      "body": {
        "productId": "int",
        "quantity": "int"
      }
    },
    {
      "method": "PUT",
      "path": "/v1/carts/items/{itemId}",
      "description": "Cập nhật quantity item trong cart",
      "body": {
        "quantity": "int"
      }
    },
    {
      "method": "DELETE",
      "path": "/v1/carts/items/{itemId}",
      "description": "Xóa item khỏi cart"
    },
    {
      "method": "POST",
      "path": "/v1/orders",
      "description": "Tạo order từ cart",
      "body": {
        "shippingAddress": "string",
        "paymentMethod": "string",
        "notes": "string"
      }
    },
    {
      "method": "GET",
      "path": "/v1/orders",
      "description": "Lấy lịch sử đơn hàng của user",
      "response": "Danh sách orders với status, total, items, tracking"
    },
    {
      "method": "GET",
      "path": "/v1/orders/{id}",
      "description": "Chi tiết đơn hàng",
      "response": "Thông tin chi tiết order, items, payment, shipping"
    }
  ]
}
```

### 1.4 Payment APIs
```json
{
  "category": "Payment Management",
  "endpoints": [
    {
      "method": "GET",
      "path": "/v1/payments",
      "description": "Lấy danh sách payments",
      "authentication": "Required"
    },
    {
      "method": "PUT",
      "path": "/v1/payments/{id}/status",
      "description": "Cập nhật trạng thái payment",
      "body": {
        "paymentStatus": "Paid|Cancelled|Pending",
        "note": "string"
      }
    },
    {
      "method": "POST",
      "path": "/v1/vnpay/create-payment",
      "description": "Tạo payment URL với VNPay",
      "body": {
        "orderId": "int",
        "amount": "decimal",
        "returnUrl": "string"
      }
    },
    {
      "method": "GET",
      "path": "/v1/vnpay/payment-callback",
      "description": "Callback từ VNPay sau khi thanh toán",
      "parameters": ["vnp_ResponseCode", "vnp_TransactionStatus", "vnp_TxnRef"]
    }
  ]
}
```

### 1.5 User & Authentication APIs
```json
{
  "category": "User & Authentication",
  "endpoints": [
    {
      "method": "POST",
      "path": "/v1/auth/register",
      "description": "Đăng ký tài khoản mới",
      "body": {
        "email": "string",
        "password": "string",
        "fullName": "string",
        "phone": "string"
      }
    },
    {
      "method": "POST",
      "path": "/v1/auth/login",
      "description": "Đăng nhập",
      "body": {
        "email": "string",
        "password": "string"
      },
      "response": "JWT token và refresh token"
    },
    {
      "method": "POST",
      "path": "/v1/auth/refresh",
      "description": "Refresh JWT token"
    },
    {
      "method": "GET",
      "path": "/v1/users/profile",
      "description": "Lấy thông tin profile user",
      "authentication": "Required"
    },
    {
      "method": "PUT",
      "path": "/v1/users/profile",
      "description": "Cập nhật profile user"
    }
  ]
}
```

## 2. Business Logic Training

### 2.1 E-commerce Flow
```text
1. User Browse Products:
   - Xem danh sách sản phẩm (/v1/products)
   - Filter theo category (/v1/products/category/{id})
   - Search sản phẩm (/v1/products/search)
   - Xem chi tiết sản phẩm (/v1/products/{id})

2. Shopping Cart Process:
   - Thêm sản phẩm vào cart (/v1/carts/items)
   - Xem cart (/v1/carts)
   - Cập nhật quantity (/v1/carts/items/{id})
   - Xóa item khỏi cart (/v1/carts/items/{id})

3. Checkout Process:
   - Tạo order từ cart (/v1/orders)
   - Chọn payment method
   - Tạo VNPay payment (/v1/vnpay/create-payment)
   - Xử lý payment callback (/v1/vnpay/payment-callback)

4. Order Management:
   - Xem lịch sử orders (/v1/orders)
   - Track order status (/v1/orders/{id})
   - Cập nhật payment status (/v1/payments/{id}/status)
```

### 2.2 Error Handling
```json
{
  "common_errors": {
    "1001": "Email already exists",
    "1004": "Session timeout - need login",
    "3001": "Product not found", 
    "4001": "Cart not found",
    "4002": "Cart item not found",
    "5001": "Order not found",
    "6001": "Invalid payment signature",
    "6003": "Invalid payment status transition"
  }
}
```

## 3. AI Training Prompts

### 3.1 System Prompt cho Gemini
```text
Bạn là AI assistant chuyên về hệ thống E-commerce ShopMate. Bạn có kiến thức đầy đủ về:

1. SẢN PHẨM:
- Quản lý danh sách sản phẩm, categories
- Tìm kiếm và filter sản phẩm
- Thống kê sản phẩm best-seller

2. GIỎ HÀNG & ĐẶT HÀNG:
- Thêm/sửa/xóa items trong cart
- Tạo và quản lý orders
- Tracking đơn hàng

3. THANH TOÁN:
- Tích hợp VNPay payment gateway
- Quản lý trạng thái payment
- Xử lý callback từ VNPay

4. USER MANAGEMENT:
- Đăng ký/đăng nhập
- Quản lý profile
- JWT authentication

Khi user hỏi về shop, hãy:
- Đưa ra câu trả lời chính xác dựa trên API documentation
- Gợi ý các tính năng liên quan
- Hướng dẫn cách sử dụng từng tính năng
- Giải thích business flow khi cần thiết
```

### 3.2 Context Examples
```text
User: "Làm sao để tìm sản phẩm iPhone?"
AI Response: "Bạn có thể tìm sản phẩm iPhone bằng cách:
1. Sử dụng search: /v1/products/search?productName=iPhone
2. Filter theo category điện thoại nếu có
3. Sắp xếp theo giá hoặc độ phổ biến
Bạn muốn tôi hướng dẫn chi tiết hơn về cách filter không?"

User: "Giỏ hàng của tôi bị lỗi"
AI Response: "Tôi sẽ giúp bạn kiểm tra giỏ hàng. Các vấn đề thường gặp:
1. Nếu không load được cart: Kiểm tra đăng nhập (/v1/auth/introspect)
2. Nếu không thêm được sản phẩm: Kiểm tra stock và API /v1/carts/items
3. Nếu quantity không cập nhật: Sử dụng PUT /v1/carts/items/{itemId}
Bạn gặp lỗi cụ thể nào?"

User: "Đơn hàng của tôi thanh toán chưa?"
AI Response: "Để kiểm tra trạng thái thanh toán:
1. Xem chi tiết order: /v1/orders/{orderId}
2. Kiểm tra payment status: Paid/Pending/Cancelled
3. Nếu đang Pending, có thể retry payment qua VNPay
Bạn có order ID không để tôi hướng dẫn kiểm tra cụ thể?"
```

## 4. Training Implementation

### 4.1 Auto-fetch API Documentation
```java
// Tạo service tự động fetch Swagger docs
@Service
public class GeminiTrainingService {
    
    public String fetchApiDocumentation() {
        // Fetch từ https://saleapp-mspd.onrender.com/v3/api-docs
        // Parse thành training data
    }
    
    public void trainWithApiDocs() {
        // Convert API docs thành training prompts
        // Send to Gemini với context
    }
}
```

### 4.2 Context Building
```java
public String buildShopContext(String userQuery) {
    StringBuilder context = new StringBuilder();
    
    // Add relevant API documentation
    if (userQuery.contains("sản phẩm") || userQuery.contains("product")) {
        context.append(getProductApiContext());
    }
    
    if (userQuery.contains("giỏ hàng") || userQuery.contains("cart")) {
        context.append(getCartApiContext());
    }
    
    if (userQuery.contains("đặt hàng") || userQuery.contains("order")) {
        context.append(getOrderApiContext());
    }
    
    if (userQuery.contains("thanh toán") || userQuery.contains("payment")) {
        context.append(getPaymentApiContext());
    }
    
    return context.toString();
}
```

## 5. Testing & Validation

### 5.1 Test Cases
```text
1. Product Search Tests:
   - "Tìm sản phẩm laptop"
   - "Sản phẩm nào bán chạy nhất?"
   - "Laptop nào trong tầm giá 20 triệu?"

2. Cart Management Tests:
   - "Thêm sản phẩm vào giỏ hàng"
   - "Xóa sản phẩm khỏi cart"
   - "Cập nhật số lượng trong giỏ"

3. Order Process Tests:
   - "Làm sao đặt hàng?"
   - "Kiểm tra đơn hàng của tôi"
   - "Hủy đơn hàng"

4. Payment Tests:
   - "Thanh toán VNPay"
   - "Đơn hàng thanh toán chưa?"
   - "Refund như thế nào?"
```

## 6. Continuous Learning

### 6.1 Auto-Update Training
```java
@Scheduled(fixedRate = 24 * 60 * 60 * 1000) // Daily
public void updateTrainingData() {
    // Fetch latest API changes
    // Update Gemini context
    // Retrain with new endpoints
}
```

### 6.2 User Feedback Integration
```java
public void processUserFeedback(String query, String response, boolean helpful) {
    if (!helpful) {
        // Log for retraining
        // Improve context for similar queries
    }
}
```

Với documentation này, AI sẽ có đầy đủ context để trả lời mọi câu hỏi về shop một cách thông minh và hữu ích!
