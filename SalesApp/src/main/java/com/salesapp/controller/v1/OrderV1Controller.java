package com.salesapp.controller.v1;

import com.salesapp.dto.request.OrderRequest;
import com.salesapp.dto.response.OrderResponse;
import com.salesapp.dto.response.ResponseObject;
import com.salesapp.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "Manage Order")
public class OrderV1Controller {

    private final OrderService orderService;

    @PostMapping("/{userId}/create")
    public ResponseObject<OrderResponse> createOrder(@PathVariable int userId, @RequestBody OrderRequest request) {
        return ResponseObject.<OrderResponse>builder()
                .status(1000)
                .message("Order created")
                .data(orderService.createOrder(userId, request))
                .build();
    }

    @GetMapping("/{userId}")
    public ResponseObject<List<OrderResponse>> getOrders(@PathVariable int userId) {
        return ResponseObject.<List<OrderResponse>>builder()
                .status(1000)
                .message("Orders retrieved")
                .data(orderService.getOrdersByUser(userId))
                .build();
    }
}
