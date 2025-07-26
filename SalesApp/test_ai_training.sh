#!/bin/bash

# Gemini AI Training Demo Script
# Cách sử dụng: ./test_ai_training.sh

BASE_URL="https://saleapp-mspd.onrender.com"
# Hoặc localhost nếu test local: BASE_URL="http://localhost:8080"

echo "🤖 === GEMINI AI TRAINING DEMO ==="
echo "Base URL: $BASE_URL"
echo ""

# 1. Test fetch API documentation
echo "📚 1. Fetching API Documentation for Training..."
curl -s -X GET "$BASE_URL/v1/ai-training/api-docs" \
  -H "Content-Type: application/json" | \
  jq -r '.data' | head -20
echo ""

# 2. Test context building for product search
echo "🔍 2. Testing Context Building - Product Search Query..."
curl -s -X POST "$BASE_URL/v1/ai-training/build-context" \
  -H "Content-Type: application/json" \
  -d '{"query": "Tôi muốn tìm sản phẩm laptop giá rẻ"}' | \
  jq -r '.data' | head -15
echo ""

# 3. Test context building for cart management
echo "🛒 3. Testing Context Building - Cart Management Query..."
curl -s -X POST "$BASE_URL/v1/ai-training/build-context" \
  -H "Content-Type: application/json" \
  -d '{"query": "Làm sao thêm sản phẩm vào giỏ hàng?"}' | \
  jq -r '.data' | head -15
echo ""

# 4. Test context building for payment
echo "💳 4. Testing Context Building - Payment Query..."
curl -s -X POST "$BASE_URL/v1/ai-training/build-context" \
  -H "Content-Type: application/json" \
  -d '{"query": "Thanh toán VNPay như thế nào?"}' | \
  jq -r '.data' | head -15
echo ""

# 5. Get common scenarios
echo "📋 5. Getting Common Training Scenarios..."
curl -s -X GET "$BASE_URL/v1/ai-training/scenarios" | \
  jq '.data'
echo ""

# 6. Test context generation with detailed analysis
echo "🧪 6. Testing Context Generation with Analysis..."
curl -s -X POST "$BASE_URL/v1/ai-training/test-context" \
  -H "Content-Type: application/json" \
  -d '{"query": "Đơn hàng của tôi thanh toán chưa?"}' | \
  jq '.data'
echo ""

echo "✅ Training system test completed!"
echo ""
echo "📖 Usage Instructions:"
echo "1. Sử dụng /v1/ai-training/api-docs để lấy full API documentation"
echo "2. Sử dụng /v1/ai-training/build-context để build context cho user query"
echo "3. Tích hợp context vào ChatMessageService để enhance AI responses"
echo "4. Monitor AI responses để improve training data"
