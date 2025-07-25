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

    @GetMapping
    public ResponseObject<List<OrderResponse>> getAllOrders() {
        return ResponseObject.<List<OrderResponse>>builder()
                .status(1000)
                .message("Orders retrieved")
                .data(orderService.getAll())
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

    @GetMapping("/detail/{orderId}")
    public ResponseObject<OrderResponse> getOrderById(@PathVariable int orderId) {
        return ResponseObject.<OrderResponse>builder()
                .status(1000)
                .message("Order detail retrieved")
                .data(orderService.getOrderById(orderId))
                .build();
    }

    // API cho admin/shipper cập nhật trạng thái giao hàng COD
    @PutMapping("/{orderId}/delivered")
    public ResponseObject<OrderResponse> markCODOrderDelivered(@PathVariable int orderId) {
        OrderResponse order = orderService.updateCODOrderDelivered(orderId, "delivered");
        return ResponseObject.<OrderResponse>builder()
                .status(1000)
                .message("COD order marked as delivered successfully")
                .data(order)
                .build();
    }

    @PutMapping("/{orderId}/processing")
    public ResponseObject<OrderResponse> markCODOrderProcessing(@PathVariable int orderId) {
        OrderResponse order = orderService.updateCODOrderDelivered(orderId, "processing");
        return ResponseObject.<OrderResponse>builder()
                .status(1000)
                .message("COD order marked as processing successfully")
                .data(order)
                .build();
    }

    @PutMapping("/{orderId}/cancelled")
    public ResponseObject<OrderResponse> markCODOrderCancelled(@PathVariable int orderId) {
        OrderResponse order = orderService.updateCODOrderDelivered(orderId, "cancelled");
        return ResponseObject.<OrderResponse>builder()
                .status(1000)
                .message("COD order marked as cancelled successfully")
                .data(order)
                .build();
    }
    //---------------------------------------------------------------------------------------
    @PutMapping("/{orderId}/failed")
    public ResponseObject<OrderResponse> markCODOrderFailed(
            @PathVariable int orderId,
            @RequestParam(defaultValue = "Customer not available") String reason) {
        OrderResponse order = orderService.updateCODOrderFailed(orderId, reason);
        return ResponseObject.<OrderResponse>builder()
                .status(1000)
                .message("COD order marked as delivery failed")
                .data(order)
                .build();
    }
}
