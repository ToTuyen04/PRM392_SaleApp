package com.salesapp.mapper;

import com.salesapp.dto.request.PaymentRequest;
import com.salesapp.dto.response.PaymentResponse;
import com.salesapp.entity.Order;
import com.salesapp.entity.Payment;
import com.salesapp.repository.OrderRepository;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "orderID.id", target = "orderID")
    PaymentResponse toPayment(Payment payment);

    List<PaymentResponse> toPayments(List<Payment> payments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paymentDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "orderID", expression = "java(orderMapperSupport.mapOrder(request.getOrderID()))")
    Payment toEntity(PaymentRequest request, @Context OrderMapperSupport orderMapperSupport);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "orderID", expression = "java(orderMapperSupport.mapOrder(request.getOrderID()))")
    void updatePayment(@MappingTarget Payment payment, PaymentRequest request, @Context OrderMapperSupport orderMapperSupport);

    @Component
    class OrderMapperSupport {
        private final OrderRepository orderRepository;

        public OrderMapperSupport(OrderRepository orderRepository) {
            this.orderRepository = orderRepository;
        }

        public Order mapOrder(Integer orderId) {
            return orderRepository.findById(orderId).orElse(null);
        }
    }
}
