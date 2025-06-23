package com.salesapp.controller.v1;

import com.salesapp.dto.request.CartItemRequest;
import com.salesapp.dto.response.CartResponse;
import com.salesapp.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.salesapp.dto.request.CartItemUpdateRequest;


@RestController
@RequestMapping("/v1/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping("/{userId}")
    public CartResponse getCart(@PathVariable int userId) {
        return cartService.getActiveCart(userId);
    }

    @PostMapping("/{userId}/add")
    public CartResponse addToCart(@PathVariable int userId, @RequestBody CartItemRequest request) {
        return cartService.addToCart(userId, request);
    }

    @PutMapping("/{userId}/update")
    public CartResponse updateCartItem(@PathVariable int userId,
                                       @RequestBody CartItemUpdateRequest request) {
        return cartService.updateCartItemQuantity(userId, request);
    }

    @DeleteMapping("/{userId}/remove/{cartItemId}")
    public CartResponse removeCartItem(@PathVariable int userId,
                                       @PathVariable int cartItemId) {
        return cartService.removeCartItem(userId, cartItemId);
    }



}
