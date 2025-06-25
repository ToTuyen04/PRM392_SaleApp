package com.salesapp.service;

import com.salesapp.dto.request.PaymentRequest;
import com.salesapp.dto.response.PaymentResponse;
import com.salesapp.entity.Payment;
import com.salesapp.exception.AppException;
import com.salesapp.exception.ErrorCode;
import com.salesapp.mapper.PaymentMapper;
import com.salesapp.mapper.PaymentMapper.OrderMapperSupport;
import com.salesapp.repository.OrderRepository;
import com.salesapp.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;
    private final OrderMapperSupport orderMapperSupport;

    public List<PaymentResponse> getAllPayments() {
        return paymentMapper.toPayments(paymentRepository.findAll());
    }

    public PaymentResponse getPaymentById(int id) {
        return paymentMapper.toPayment(paymentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_FOUND)));
    }

    public PaymentResponse createPayment(PaymentRequest request) {
        // Order validation vẫn nên giữ
        orderRepository.findById(request.getOrderID())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Dùng mapper với context
        Payment payment = paymentMapper.toEntity(request, orderMapperSupport);
        paymentRepository.save(payment);

        return paymentMapper.toPayment(payment);
    }
}
