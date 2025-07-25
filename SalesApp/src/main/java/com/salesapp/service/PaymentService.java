package com.salesapp.service;

import com.salesapp.dto.request.PaymentRequest;
import com.salesapp.dto.request.PaymentStatusUpdateRequest;
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

import java.time.Instant;
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

    public PaymentResponse updatePaymentStatus(int paymentId, PaymentStatusUpdateRequest request) {
        // Tìm payment theo ID
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_FOUND));

        // Kiểm tra trạng thái hiện tại để tránh cập nhật không hợp lệ
        String currentStatus = payment.getPaymentStatus();
        String newStatus = request.getPaymentStatus();

        // Business logic: Không cho phép chuyển từ Paid về Pending hoặc Cancelled
        if ("Paid".equals(currentStatus) && ("Pending".equals(newStatus) || "Cancelled".equals(newStatus))) {
            throw new AppException(ErrorCode.INVALID_PAYMENT_STATUS_TRANSITION);
        }

        // Cập nhật status
        payment.setPaymentStatus(newStatus);
        
        // Nếu chuyển sang Paid, cập nhật payment date
        if ("Paid".equals(newStatus) && !"Paid".equals(currentStatus)) {
            payment.setPaymentDate(Instant.now());
        }

        // Lưu và trả về
        paymentRepository.save(payment);
        return paymentMapper.toPayment(payment);
    }
}
