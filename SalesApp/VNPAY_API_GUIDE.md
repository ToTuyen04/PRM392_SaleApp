# VNPay Integration API Guide

## Tổng quan
API VNPay đã được tích hợp vào SalesApp để xử lý thanh toán trực tuyến. API này bao gồm các endpoint để tạo URL thanh toán và xử lý callback từ VNPay.

## Cấu hình

### 1. VNPay Configuration
File: `src/main/java/com/salesapp/config/VNPAYConfig.java`

**Lưu ý quan trọng**: Cần thay đổi các thông tin sau theo tài khoản VNPay thực tế của bạn:
```java
public static String vnp_TmnCode = "I4CFWC18"; // Thay bằng TMN Code của bạn
public static String vnp_HashSecret = "A72HI1BP870VL7NBUCBJ9ZKRXMBUN1LX"; // Thay bằng Hash Secret của bạn
```

### 2. Error Code
Đã thêm error code cho VNPay:
```java
PAYMENT_INVALID_SIGN(6001, "Payment signature is invalid", HttpStatus.BAD_REQUEST)
```

## API Endpoints

### 1. Tạo URL thanh toán VNPay
**POST** `/v1/vnpay/create-payment`

**Parameters:**
- `amount` (int): Số tiền thanh toán (VND)
- `orderInfo` (String): Thông tin đơn hàng
- `returnUrl` (String, optional): URL callback sau khi thanh toán

**Response:**
```json
{
    "status": 1000,
    "message": "VNPay payment URL created successfully",
    "data": "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?..."
}
```

### 2. Tạo thanh toán cho order cụ thể
**POST** `/v1/vnpay/create-payment-for-order/{orderId}`

**Parameters:**
- `orderId` (int): ID của order
- `returnUrl` (String, optional): URL callback sau khi thanh toán

### 3. Tạo order và payment URL cùng lúc
**POST** `/v1/vnpay/create-order-and-payment/{userId}`

**Request Body:**
```json
{
    "paymentMethod": "VNPAY",
    "billingAddress": "123 Main St, City"
}
```

### 4. Xử lý callback từ VNPay
**GET** `/v1/vnpay/payment-callback`

Endpoint này được VNPay gọi sau khi người dùng hoàn tất thanh toán.

### 5. Xử lý kết quả thanh toán
**GET** `/v1/vnpay/payment-result?orderId={orderId}`

**Response:**
```json
{
    "status": 1000,
    "message": "Payment successful and order updated",
    "data": {
        "orderInfo": "Thanh toan don hang #123",
        "paymentTime": "20231201123000",
        "transactionID": "14123456",
        "totalPrice": "10000000",
        "status": 1
    }
}
```

## Flow thanh toán

### Cách 1: Thanh toán cho order đã có
1. Tạo order bằng API `/v1/orders/{userId}/create`
2. Gọi `/v1/vnpay/create-payment-for-order/{orderId}` để lấy URL thanh toán
3. Redirect user đến URL VNPay
4. VNPay callback về `/v1/vnpay/payment-result?orderId={orderId}`
5. Order được cập nhật status tự động

### Cách 2: Tạo order và thanh toán cùng lúc
1. Gọi `/v1/vnpay/create-order-and-payment/{userId}` với thông tin order
2. Nhận URL thanh toán và redirect user
3. Xử lý callback tương tự cách 1

## Status codes

- `status: 1` - Thanh toán thành công
- `status: 0` - Thanh toán thất bại
- `status: -1` - Chữ ký không hợp lệ

## Lưu ý khi triển khai

1. **Bảo mật**: Không để lộ `vnp_HashSecret` trong code frontend
2. **URL Callback**: Đảm bảo server có thể nhận callback từ VNPay
3. **Testing**: Sử dụng sandbox environment trước khi chuyển production
4. **Error Handling**: Xử lý các trường hợp thanh toán thất bại
5. **Logging**: Log các transaction để debug và audit

## Testing

Để test API, bạn có thể sử dụng Postman hoặc curl:

```bash
# Tạo payment URL
curl -X POST "http://localhost:8080/v1/vnpay/create-payment" \
  -d "amount=100000&orderInfo=Test Order&returnUrl=http://localhost:3000/result"

# Tạo order và payment
curl -X POST "http://localhost:8080/v1/vnpay/create-order-and-payment/1" \
  -H "Content-Type: application/json" \
  -d '{"paymentMethod":"VNPAY","billingAddress":"Test Address"}'
```
