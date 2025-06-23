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
        order.setOrderStatus("processing");
        order.setOrderDate(Instant.now());

        orderRepository.save(order);

        // Tạo Payment gắn vào Order
        Payment payment = new Payment();
        payment.setOrderID(order);
        payment.setAmount(cart.getTotalPrice());
        payment.setPaymentDate(Instant.now());
        payment.setPaymentStatus("Paid");

        paymentRepository.save(payment);
        order.getPayments().add(payment);

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
}
