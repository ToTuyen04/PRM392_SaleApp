package com.salesapp.controller.v1;

import com.salesapp.dto.request.OrderRequest;
import com.salesapp.dto.response.OrderResponse;
import com.salesapp.dto.response.OrderDetailResponse;
import com.salesapp.dto.response.ResponseObject;
import com.salesapp.dto.response.VNPayResponse;
import com.salesapp.service.OrderService;
import com.salesapp.service.VNPAYService;
import com.salesapp.service.VNPayServiceDev;
import com.salesapp.config.ServerConfig;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/v1/vnpay")
@RequiredArgsConstructor
@Tag(name = "VNPay", description = "VNPay Payment Integration")
public class VNPAYController {

    private final VNPayServiceDev vnPayService;
    private final OrderService orderService;
    private final ServerConfig serverConfig;

    // Tạo URL thanh toán VNPay
    @PostMapping("/create-payment")
    public ResponseObject<String> createPayment(
            @RequestParam("amount") int totalAmount,
            @RequestParam("orderInfo") String orderInfo,
            @RequestParam(value = "returnUrl", defaultValue = "http://localhost:3000/payment-result") String returnUrl,
            HttpServletRequest request) {
        
        String vnpayUrl = vnPayService.createOrder(request, totalAmount, orderInfo, returnUrl);
        
        return ResponseObject.<String>builder()
                .status(1000)
                .message("VNPay payment URL created successfully")
                .data(vnpayUrl)
                .build();
    }

    // Xử lý callback từ VNPay sau khi thanh toán
    @GetMapping("/payment-callback")
    public void paymentCallback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        VNPayResponse vnPayResponse = vnPayService.orderReturn(request);

        // Lấy orderId từ vnp_OrderInfo
        String orderInfo = request.getParameter("vnp_OrderInfo");
        int orderId = 0;
        try {
            orderId = Integer.parseInt(orderInfo);
        } catch (NumberFormatException e) {
            System.out.println("Cannot parse orderId from orderInfo: " + orderInfo);
        }

        // Lấy mobile return URL từ parameter
        String mobileReturnUrl = request.getParameter("mobileReturnUrl");
        if (mobileReturnUrl == null) {
            mobileReturnUrl = "shopmate://payment-result"; // Default deep link
        }

        String redirectUrl;

        if (vnPayResponse.getStatus() == 1) {
            // Thanh toán thành công - cập nhật order
            if (orderId > 0) {
                try {
                    orderService.updateOrderAfterVNPaySuccess(orderId, vnPayResponse.getTransactionID());
                    System.out.println("Order " + orderId + " updated successfully");
                } catch (Exception e) {
                    System.out.println("Error updating order: " + e.getMessage());
                }
            }

            // Redirect về mobile app với kết quả thành công
            redirectUrl = mobileReturnUrl + "?status=success&orderId=" + orderId +
                         "&transactionId=" + vnPayResponse.getTransactionID();

        } else if (vnPayResponse.getStatus() == 0) {
            // Thanh toán thất bại - hủy order
            if (orderId > 0) {
                try {
                    orderService.cancelOrder(orderId);
                    System.out.println("Order " + orderId + " cancelled");
                } catch (Exception e) {
                    System.out.println("Error cancelling order: " + e.getMessage());
                }
            }

            // Redirect về mobile app với kết quả thất bại
            redirectUrl = mobileReturnUrl + "?status=failed&orderId=" + orderId +
                         "&error=payment_failed";

        } else {
            // Chữ ký không hợp lệ
            redirectUrl = mobileReturnUrl + "?status=error&orderId=" + orderId +
                         "&error=invalid_signature";
        }

        // Redirect về mobile app
        response.sendRedirect(redirectUrl);
    }

    // Endpoint để tạo thanh toán cho một order cụ thể
    @PostMapping("/create-payment-for-order/{orderId}")
    public ResponseObject<String> createPaymentForOrder(
            @PathVariable int orderId,
            @RequestParam(value = "returnUrl", defaultValue = "http://localhost:3000/payment-result") String returnUrl,
            HttpServletRequest request) {

        // Lấy thông tin order từ database
        var orderResponse = orderService.getOrderById(orderId);
        String orderInfo = String.valueOf(orderId); // Chỉ gửi orderId để dễ parse

        // Lấy amount từ cart của order (cần convert BigDecimal to int)
        int amount = orderResponse.getPayments().isEmpty() ?
            100000 : // Default nếu chưa có payment
            orderResponse.getPayments().get(0).getAmount().intValue();

        String vnpayUrl = vnPayService.createOrder(request, amount, orderInfo, returnUrl);

        return ResponseObject.<String>builder()
                .status(1000)
                .message("VNPay payment URL created for order #" + orderId)
                .data(vnpayUrl)
                .build();
    }

    // Endpoint để xử lý kết quả thanh toán và cập nhật order
    @GetMapping("/payment-result")
    public ResponseObject<VNPayResponse> handlePaymentResult(
            @RequestParam int orderId,
            HttpServletRequest request) {

        VNPayResponse vnPayResponse = vnPayService.orderReturn(request);

        if (vnPayResponse.getStatus() == 1) {
            // Thanh toán thành công - cập nhật order
            orderService.updateOrderAfterVNPaySuccess(orderId, vnPayResponse.getTransactionID());

            return ResponseObject.<VNPayResponse>builder()
                    .status(1000)
                    .message("Payment successful and order updated")
                    .data(vnPayResponse)
                    .build();
        } else if (vnPayResponse.getStatus() == 0) {
            // Thanh toán thất bại - hủy order
            orderService.cancelOrder(orderId);

            return ResponseObject.<VNPayResponse>builder()
                    .status(2000)
                    .message("Payment failed and order cancelled")
                    .data(vnPayResponse)
                    .build();
        } else {
            // Chữ ký không hợp lệ
            return ResponseObject.<VNPayResponse>builder()
                    .status(3000)
                    .message("Invalid payment signature")
                    .data(vnPayResponse)
                    .build();
        }
    }

    // Endpoint để tạo order và payment URL cùng lúc
    @PostMapping("/create-order-and-payment/{userId}")
    public ResponseObject<String> createOrderAndPayment(
            @PathVariable int userId,
            @RequestBody OrderRequest orderRequest,
            @RequestParam(value = "mobileReturnUrl", defaultValue = "shopmate://payment-result") String mobileReturnUrl,
            HttpServletRequest request) {

        // Tạo order trước
        OrderResponse orderResponse = orderService.createOrderForVNPay(userId, orderRequest);

        // Tạo payment URL với backend callback
        String orderInfo = String.valueOf(orderResponse.getId()); // Chỉ gửi orderId để dễ parse

        // Lấy amount từ payment trong order (VNPay yêu cầu đơn vị xu, nên nhân 100)
        int amount = 100000; // Default amount (100,000 VND)
        if (orderResponse.getPayments() != null && !orderResponse.getPayments().isEmpty()) {
            // Lấy amount từ payment và convert sang xu (VNPay format)
            java.math.BigDecimal paymentAmount = orderResponse.getPayments().get(0).getAmount();
            amount = paymentAmount.multiply(new java.math.BigDecimal(100)).intValue();

            System.out.println("=== PAYMENT AMOUNT DEBUG ===");
            System.out.println("Cart Total (VND): " + paymentAmount);
            System.out.println("VNPay Amount (xu): " + amount);
            System.out.println("===========================");
        } else {
            System.out.println("WARNING: No payment found, using default amount: " + amount);
        }

        // VNPay sẽ callback về backend, sau đó backend redirect về mobile app
        // Sử dụng ServerConfig để lấy base URL
        String serverUrl = serverConfig.getBaseUrl();
        String backendCallbackUrl = serverUrl + "/v1/vnpay/payment-callback?mobileReturnUrl=" +
                java.net.URLEncoder.encode(mobileReturnUrl, java.nio.charset.StandardCharsets.UTF_8);

        String vnpayUrl = vnPayService.createOrder(request, amount, orderInfo, backendCallbackUrl);

        return ResponseObject.<String>builder()
                .status(1000)
                .message("Order created and VNPay payment URL generated")
                .data(vnpayUrl)
                .build();
    }

    // Endpoint để lấy order detail với thông tin đầy đủ
    @GetMapping("/order-detail/{orderId}")
    public ResponseObject<OrderDetailResponse> getOrderDetail(@PathVariable int orderId) {
        OrderDetailResponse orderDetail = orderService.getOrderDetailById(orderId);

        return ResponseObject.<OrderDetailResponse>builder()
                .status(1000)
                .message("Order detail retrieved successfully")
                .data(orderDetail)
                .build();
    }

    // ========== TEST ENDPOINTS ==========

    @PostMapping("/test-callback")
    public ResponseObject<String> testCallback(
            @RequestParam int orderId,
            @RequestParam(defaultValue = "00") String responseCode,
            @RequestParam(defaultValue = "00") String transactionStatus) {

        // Tạo mock VNPay parameters
        String testUrl = String.format(
            "/v1/vnpay/payment-callback?vnp_Amount=%d&vnp_BankCode=NCB&vnp_OrderInfo=%d&vnp_ResponseCode=%s&vnp_TransactionStatus=%s&vnp_TransactionNo=%d&vnp_SecureHash=test_hash",
            100000 * 100, // amount * 100
            orderId,
            responseCode,
            transactionStatus,
            System.currentTimeMillis()
        );

        return ResponseObject.<String>builder()
                .status(1000)
                .message("Test callback URL generated")
                .data("http://localhost:8080" + testUrl)
                .build();
    }

    @GetMapping("/test-payment-scenarios")
    public ResponseObject<Object> testPaymentScenarios(@RequestParam int orderId) {
        String baseUrl = "http://localhost:8080";
        long timestamp = System.currentTimeMillis();

        // Tạo các scenario test khác nhau
        Object scenarios = new Object() {
            public final String successCallback = String.format(
                "%s/v1/vnpay/payment-callback?vnp_Amount=10000000&vnp_BankCode=NCB&vnp_OrderInfo=%d&vnp_ResponseCode=00&vnp_TransactionStatus=00&vnp_TransactionNo=%d&vnp_SecureHash=test_hash",
                baseUrl, orderId, timestamp
            );

            public final String failedCallback = String.format(
                "%s/v1/vnpay/payment-callback?vnp_Amount=10000000&vnp_BankCode=NCB&vnp_OrderInfo=%d&vnp_ResponseCode=24&vnp_TransactionStatus=02&vnp_TransactionNo=%d&vnp_SecureHash=test_hash",
                baseUrl, orderId, timestamp + 1
            );

            public final String successResult = String.format(
                "%s/v1/vnpay/payment-result?orderId=%d&vnp_Amount=10000000&vnp_ResponseCode=00&vnp_TransactionStatus=00&vnp_TransactionNo=%d&vnp_SecureHash=test_hash",
                baseUrl, orderId, timestamp + 2
            );

            public final String failedResult = String.format(
                "%s/v1/vnpay/payment-result?orderId=%d&vnp_Amount=10000000&vnp_ResponseCode=24&vnp_TransactionStatus=02&vnp_TransactionNo=%d&vnp_SecureHash=test_hash",
                baseUrl, orderId, timestamp + 3
            );
        };

        return ResponseObject.builder()
                .status(1000)
                .message("Test scenarios generated for order " + orderId)
                .data(scenarios)
                .build();
    }


}
