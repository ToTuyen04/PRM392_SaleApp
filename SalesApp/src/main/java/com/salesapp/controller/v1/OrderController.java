package com.salesapp.controller.v1;

import com.salesapp.dto.request.OrderRequest;
import com.salesapp.dto.response.OrderResponse;
import com.salesapp.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{userId}/create")
    public OrderResponse createOrder(@PathVariable int userId,
                                     @RequestBody OrderRequest request) {
        return orderService.createOrder(userId, request);
    }

    @GetMapping("/{userId}")
    public List<OrderResponse> getOrders(@PathVariable int userId) {
        return orderService.getOrdersByUser(userId);
    }
}

