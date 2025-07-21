package com.salesapp.controller.v1;

import com.salesapp.dto.request.CartItemRequest;
import com.salesapp.dto.response.CartResponse;
import com.salesapp.dto.response.ResponseObject;
import com.salesapp.service.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.salesapp.dto.request.CartItemUpdateRequest;

@Tag(name = "Cart", description = "Manage Cart Items")
@RestController
@RequestMapping("/v1/carts")
@RequiredArgsConstructor
public class CartV1Controller {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseObject<CartResponse> getCart(@PathVariable int userId) {
        CartResponse cart = cartService.getActiveCart(userId);
        return ResponseObject.<CartResponse>builder()
                .status(1000)
                .message("Cart retrieved successfully")
                .data(cart)
                .build();
    }

    @PostMapping("/{userId}/add")
    public ResponseObject<CartResponse> addToCart(@PathVariable int userId,
                                                  @RequestBody CartItemRequest request) {
        CartResponse cart = cartService.addToCart(userId, request);
        return ResponseObject.<CartResponse>builder()
                .status(1000)
                .message("Item added to cart successfully")
                .data(cart)
                .build();
    }

    @PutMapping("/{userId}/update")
    public ResponseObject<CartResponse> updateCartItem(@PathVariable int userId,
                                                       @RequestBody CartItemUpdateRequest request) {
        CartResponse cart = cartService.updateCartItemQuantity(userId, request);
        return ResponseObject.<CartResponse>builder()
                .status(1000)
                .message("Cart item updated successfully")
                .data(cart)
                .build();
    }

    @DeleteMapping("/{userId}/remove/{cartItemId}")
    public ResponseObject<CartResponse> removeCartItem(@PathVariable int userId,
                                                       @PathVariable int cartItemId) {
        CartResponse cart = cartService.removeCartItem(userId, cartItemId);
        return ResponseObject.<CartResponse>builder()
                .status(1000)
                .message("Cart item removed successfully")
                .data(cart)
                .build();
    }

    @PostMapping("/{userId}/cleanup")
    public ResponseObject<CartResponse> cleanupCart(@PathVariable int userId) {
        CartResponse cart = cartService.cleanupDuplicateCartItems(userId);
        return ResponseObject.<CartResponse>builder()
                .status(1000)
                .message("Cart cleaned up successfully - duplicate items merged")
                .data(cart)
                .build();
    }
}
