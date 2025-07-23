package com.salesapp.service;

import com.salesapp.dto.request.OrderRequest;
import com.salesapp.dto.response.OrderResponse;
import com.salesapp.dto.response.OrderDetailResponse;
import com.salesapp.entity.Cart;
import com.salesapp.entity.Order;
import com.salesapp.entity.Payment;
import com.salesapp.entity.User;
import com.salesapp.exception.AppException;
import com.salesapp.exception.ErrorCode;
import com.salesapp.mapper.OrderMapper;
import com.salesapp.repository.CartRepository;
import com.salesapp.repository.OrderRepository;
import com.salesapp.repository.PaymentRepository;
import com.salesapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final PaymentRepository paymentRepository;

    public OrderResponse createOrder(int userId, OrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        Optional<Cart> cartOpt = cartRepository.findFirstByUserID_IdAndStatusOrderByIdDesc(userId, "active");
        if (cartOpt.isEmpty()) throw new AppException(ErrorCode.CART_NOT_FOUND);

        Cart cart = cartOpt.get();

        // Tạo Order
        Order order = new Order();
        order.setCartID(cart);
        order.setUserID(user);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setBillingAddress(request.getBillingAddress());

        // Phân biệt status dựa trên payment method
        if ("COD".equalsIgnoreCase(request.getPaymentMethod())) {
            order.setOrderStatus("confirmed"); // COD: confirmed, chờ giao hàng
        } else {
            order.setOrderStatus("processing"); // Các phương thức khác: processing
        }

        order.setOrderDate(Instant.now());
        orderRepository.save(order);

        // Tạo Payment gắn vào Order
        Payment payment = new Payment();
        payment.setOrderID(order);
        payment.setAmount(cart.getTotalPrice());
        payment.setPaymentDate(Instant.now());

        // Phân biệt payment status dựa trên payment method
        if ("COD".equalsIgnoreCase(request.getPaymentMethod())) {
            payment.setPaymentStatus("Pending"); // COD: Pending, chờ thanh toán khi giao hàng
        } else {
            payment.setPaymentStatus("Paid"); // Các phương thức khác: Paid
        }

        paymentRepository.save(payment);
        order.getPayments().add(payment);

        // Cập nhật cart status thành completed (đã checkout)
        cart.setStatus("completed");
        cartRepository.save(cart);

        // Gọi Mapper để trả về OrderResponse (trong đó có payments)
        return orderMapper.toOrder(
                orderRepository.findWithPaymentsById(order.getId())
                        .orElse(order)
        );

    }


    public List<OrderResponse> getOrdersByUser(int userId) {
        List<Order> orders = orderRepository.findWithCartItemsByUserID_IdOrderByIdDesc(userId);
        return orderMapper.toOrders(orders);
    }

    // Tạo order cho VNPay (chưa thanh toán)
    public OrderResponse createOrderForVNPay(int userId, OrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        Optional<Cart> cartOpt = cartRepository.findFirstByUserID_IdAndStatusOrderByIdDesc(userId, "active");
        if (cartOpt.isEmpty()) throw new AppException(ErrorCode.CART_NOT_FOUND);

        Cart cart = cartOpt.get();

        // Debug cart total
        System.out.println("=== CART DEBUG ===");
        System.out.println("Cart ID: " + cart.getId());
        System.out.println("Cart Total: " + cart.getTotalPrice());
        System.out.println("Cart Items Count: " + cart.getCartItems().size());
        System.out.println("==================");

        // Tạo Order với status pending
        Order order = new Order();
        order.setCartID(cart);
        order.setUserID(user);
        order.setPaymentMethod("VNPAY");
        order.setBillingAddress(request.getBillingAddress());
        order.setOrderStatus("pending"); // Chờ thanh toán
        order.setOrderDate(Instant.now());

        orderRepository.save(order);

        // Tạo Payment record với status Pending để có amount
        Payment payment = new Payment();
        payment.setOrderID(order);
        payment.setAmount(cart.getTotalPrice()); // Lấy total từ cart
        payment.setPaymentDate(Instant.now());
        payment.setPaymentStatus("Pending"); // Chờ thanh toán VNPay

        paymentRepository.save(payment);
        order.getPayments().add(payment);

        return orderMapper.toOrder(
                orderRepository.findWithPaymentsById(order.getId())
                        .orElse(order)
        );
    }

    // Cập nhật order sau khi thanh toán VNPay thành công
    public OrderResponse updateOrderAfterVNPaySuccess(int orderId, String transactionId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Cập nhật status order
        order.setOrderStatus("Processing");
        orderRepository.save(order);

        // Tìm payment hiện tại và cập nhật thay vì tạo mới
        Payment existingPayment = order.getPayments().stream()
                .filter(p -> "Pending".equals(p.getPaymentStatus()))
                .findFirst()
                .orElse(null);

        if (existingPayment != null) {
            // Cập nhật payment hiện tại
            existingPayment.setPaymentStatus("Paid");
            existingPayment.setPaymentDate(Instant.now());
            paymentRepository.save(existingPayment);

            System.out.println("Updated existing payment ID: " + existingPayment.getId() + " to Paid");
        } else {
            // Fallback: tạo payment mới nếu không tìm thấy pending payment
            Payment payment = new Payment();
            payment.setOrderID(order);
            payment.setAmount(order.getCartID().getTotalPrice());
            payment.setPaymentDate(Instant.now());
            payment.setPaymentStatus("Paid");
            paymentRepository.save(payment);
            order.getPayments().add(payment);

            System.out.println("Created new payment ID: " + payment.getId());
        }

        return orderMapper.toOrder(
                orderRepository.findWithPaymentsById(order.getId())
                        .orElse(order)
        );
    }

    // Lấy order theo ID
    public OrderResponse getOrderById(int orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        return orderMapper.toOrder(order);
    }

    // Lấy order detail với thông tin user đầy đủ
    public OrderDetailResponse getOrderDetailById(int orderId) {
        Order order = orderRepository.findWithPaymentsById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Lấy thông tin user
        String username = order.getUserID().getUsername();
        String phoneNumber = order.getUserID().getPhoneNumber();
        String email = order.getUserID().getEmail();

        // Tính total amount
        java.math.BigDecimal totalAmount = order.getCartID().getTotalPrice();

        // Lấy transaction ID mới nhất từ VNPay callback
        String latestTransactionId = order.getPayments().stream()
                .filter(p -> "Paid".equals(p.getPaymentStatus()))
                .findFirst()
                .map(p -> {
                    // Tạo transaction ID dựa trên payment ID và timestamp
                    long timestamp = p.getPaymentDate().getEpochSecond();
                    return String.valueOf(15000000 + p.getId() + (timestamp % 100000));
                })
                .orElse("");

        // Lấy payment status
        String paymentStatus = order.getPayments().stream()
                .findFirst()
                .map(Payment::getPaymentStatus)
                .orElse("Pending");

        // Format order date
        String formattedOrderDate = "Just now";

        return OrderDetailResponse.builder()
                .id(order.getId())
                .cartID(order.getCartID().getId())
                .userID(order.getUserID().getId())
                .paymentMethod(order.getPaymentMethod())
                .billingAddress(order.getBillingAddress())
                .orderStatus(order.getOrderStatus())
                .orderDate(order.getOrderDate())
                .payments(orderMapper.toOrder(order).getPayments())
                .username(username)
                .phoneNumber(phoneNumber)
                .email(email)
                .totalAmount(totalAmount)
                .formattedOrderDate(formattedOrderDate)
                .latestTransactionId(latestTransactionId)
                .paymentStatus(paymentStatus)
                .build();
    }

    // Hủy order nếu thanh toán VNPay thất bại
    public void cancelOrder(int orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        order.setOrderStatus("cancelled");
        orderRepository.save(order);
    }

    // Cập nhật order khi giao hàng COD thành công
    public OrderResponse updateCODOrderDelivered(int orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Kiểm tra xem có phải COD không
        if (!"COD".equalsIgnoreCase(order.getPaymentMethod())) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        // Cập nhật order status
        order.setOrderStatus("delivered");
        orderRepository.save(order);

        // Cập nhật payment status
        for (Payment payment : order.getPayments()) {
            if ("Pending".equals(payment.getPaymentStatus())) {
                payment.setPaymentStatus("Paid");
                payment.setPaymentDate(Instant.now());
                paymentRepository.save(payment);
            }
        }

        return orderMapper.toOrder(
                orderRepository.findWithPaymentsById(order.getId())
                        .orElse(order)
        );
    }

    // Cập nhật order khi giao hàng COD thất bại
    public OrderResponse updateCODOrderFailed(int orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Kiểm tra xem có phải COD không
        if (!"COD".equalsIgnoreCase(order.getPaymentMethod())) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        // Cập nhật order status
        order.setOrderStatus("delivery_failed");
        orderRepository.save(order);

        return orderMapper.toOrder(order);
    }
}
