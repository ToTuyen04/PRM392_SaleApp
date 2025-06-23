package com.salesapp.mapper;

import com.salesapp.dto.request.PaymentRequest;
import com.salesapp.dto.response.PaymentResponse;
import com.salesapp.entity.Order;
import com.salesapp.entity.Payment;
import com.salesapp.repository.OrderRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring", uses = PaymentMapper.OrderMapperSupport.class)
public abstract class PaymentMapper {

    @Autowired
    protected OrderMapperSupport orderMapperSupport;

    @Mapping(source = "orderID.id", target = "orderID")
    public abstract PaymentResponse toPayment(Payment payment);

    public abstract List<PaymentResponse> toPayments(List<Payment> payments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paymentDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "orderID", expression = "java(orderMapperSupport.mapOrder(request.getOrderID()))")
    public abstract Payment toEntity(PaymentRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "orderID", expression = "java(orderMapperSupport.mapOrder(request.getOrderID()))")
    public abstract void updatePayment(@MappingTarget Payment payment, PaymentRequest request);

    @Component
    public static class OrderMapperSupport {
        @Autowired
        private OrderRepository orderRepository;

        public Order mapOrder(Integer orderId) {
            return orderRepository.findById(orderId).orElse(null);
        }
    }
}
