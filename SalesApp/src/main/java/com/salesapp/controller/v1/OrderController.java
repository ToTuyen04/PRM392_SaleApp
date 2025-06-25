package com.salesapp.controller.v1;

import com.salesapp.dto.request.OrderRequest;
import com.salesapp.dto.response.OrderResponse;
import com.salesapp.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Order", description = "Manage Order")
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

