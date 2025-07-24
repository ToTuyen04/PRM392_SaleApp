package com.salesapp.service;

import com.salesapp.dto.request.OrderRequest;
import com.salesapp.dto.response.OrderResponse;
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

        Cart cart = cartRepository.findByUserID_IdAndStatus(userId, "active");
        if (cart == null) throw new AppException(ErrorCode.CART_NOT_FOUND);

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
        List<Order> orders = orderRepository.findByUserID_Id(userId);
        return orderMapper.toOrders(orders);
    }

    // Tạo order cho VNPay (chưa thanh toán)
    public OrderResponse createOrderForVNPay(int userId, OrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        Cart cart = cartRepository.findByUserID_IdAndStatus(userId, "active");
        if (cart == null) throw new AppException(ErrorCode.CART_NOT_FOUND);

        // Tạo Order với status pending
        Order order = new Order();
        order.setCartID(cart);
        order.setUserID(user);
        order.setPaymentMethod("VNPAY");
        order.setBillingAddress(request.getBillingAddress());
        order.setOrderStatus("pending"); // Chờ thanh toán
        order.setOrderDate(Instant.now());

        orderRepository.save(order);

        return orderMapper.toOrder(order);
    }

    // Cập nhật order sau khi thanh toán VNPay thành công
    public OrderResponse updateOrderAfterVNPaySuccess(int orderId, String transactionId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Cập nhật status order
        order.setOrderStatus("Processing");
        orderRepository.save(order);

        // Tạo Payment record
        Payment payment = new Payment();
        payment.setOrderID(order);
        payment.setAmount(order.getCartID().getTotalPrice());
        payment.setPaymentDate(Instant.now());
        payment.setPaymentStatus("Paid");

        paymentRepository.save(payment);
        order.getPayments().add(payment);

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
