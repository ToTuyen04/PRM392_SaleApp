# Payment Status Update API Documentation

## Endpoint
```
PUT /v1/payments/{id}/status
```

## Description
Cập nhật trạng thái thanh toán (Payment Status) cho một payment cụ thể. API này cho phép chuyển đổi trạng thái thanh toán giữa các trạng thái: `Paid`, `Cancelled`, và `Pending`.

## Request Parameters

### Path Parameters
- `id` (integer, required): ID của payment cần cập nhật

### Request Body
```json
{
  "paymentStatus": "Paid",
  "note": "Payment completed via VNPay"
}
```

#### Fields:
- `paymentStatus` (string, required): Trạng thái mới của payment
  - Allowed values: `"Paid"`, `"Cancelled"`, `"Pending"`
- `note` (string, optional): Ghi chú bổ sung cho việc cập nhật trạng thái

## Response

### Success Response (200 OK)
```json
{
  "status": 1000,
  "message": "Payment status updated successfully",
  "data": {
    "id": 1,
    "orderID": {
      "id": 123,
      "orderDate": "2024-01-15T10:30:00Z",
      "totalAmount": 100000
    },
    "amount": 100000,
    "paymentDate": "2024-01-15T10:35:00Z",
    "paymentStatus": "Paid"
  }
}
```

### Error Responses

#### 400 Bad Request - Invalid Status Transition
```json
{
  "status": 6003,
  "message": "Invalid payment status transition"
}
```

#### 400 Bad Request - Invalid Status Value
```json
{
  "status": 9001,
  "message": "Payment status must be Paid, Cancelled, or Pending"
}
```

#### 404 Not Found - Payment Not Found
```json
{
  "status": 4002,
  "message": "Cart item not found"
}
```

## Business Rules

1. **Status Transition Validation**: Không cho phép chuyển từ `"Paid"` về `"Pending"` hoặc `"Cancelled"`
2. **Payment Date Update**: Khi chuyển status thành `"Paid"`, payment date sẽ được tự động cập nhật thành thời điểm hiện tại
3. **Valid Status Values**: Chỉ chấp nhận `"Paid"`, `"Cancelled"`, hoặc `"Pending"`

## Usage Examples

### 1. Mark payment as Paid
```bash
curl -X PUT http://localhost:8080/v1/payments/1/status \
  -H "Content-Type: application/json" \
  -d '{
    "paymentStatus": "Paid",
    "note": "VNPay payment successful"
  }'
```

### 2. Cancel a payment
```bash
curl -X PUT http://localhost:8080/v1/payments/1/status \
  -H "Content-Type: application/json" \
  -d '{
    "paymentStatus": "Cancelled",
    "note": "User cancelled the payment"
  }'
```

### 3. Reset payment to Pending
```bash
curl -X PUT http://localhost:8080/v1/payments/1/status \
  -H "Content-Type: application/json" \
  -d '{
    "paymentStatus": "Pending",
    "note": "Reset for retry"
  }'
```

## Integration with VNPay

Khi sử dụng với VNPay callback:
- VNPay callback có thể gọi API này để cập nhật status thành `"Paid"` khi thanh toán thành công
- Khi user hủy thanh toán trên VNPay, có thể cập nhật status thành `"Cancelled"`

## Notes

- API này yêu cầu authentication (JWT token)
- Validate payment status transition để tránh các trạng thái không hợp lệ
- Payment date chỉ được cập nhật khi chuyển sang trạng thái `"Paid"`
- Tất cả thay đổi được lưu ngay vào database
