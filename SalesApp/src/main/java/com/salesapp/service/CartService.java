package com.salesapp.service;

import com.salesapp.dto.request.CartItemRequest;
import com.salesapp.dto.request.CartItemUpdateRequest;
import com.salesapp.dto.response.CartResponse;
import com.salesapp.entity.Cart;
import com.salesapp.entity.CartItem;
import com.salesapp.entity.Product;
import com.salesapp.entity.User;
import com.salesapp.exception.AppException;
import com.salesapp.exception.ErrorCode;
import com.salesapp.mapper.CartMapper;
import com.salesapp.repository.CartItemRepository;
import com.salesapp.repository.CartRepository;
import com.salesapp.repository.ProductRepository;
import com.salesapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.math.BigDecimal;
import java.util.LinkedHashSet;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    public CartResponse getActiveCart(int userId) {
        Cart cart = cartRepository.findByUserID_IdAndStatus(userId, "active");
        if (cart == null) {
            cart = new Cart();
            cart.setUserID(userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND)));
            cart.setStatus("active");
            cart.setTotalPrice(BigDecimal.ZERO);
            cart.setCartItems(new LinkedHashSet<>());
            cartRepository.save(cart);
        }
        return mapCart(cart);
    }

    public CartResponse addToCart(int userId, CartItemRequest request) {
        // Tìm giỏ hàng active của user, nếu không có thì tạo mới
        Cart cart = cartRepository.findByUserID_IdAndStatus(userId, "active");
        if (cart == null) {
            cart = new Cart();
            cart.setUserID(userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND)));
            cart.setStatus("active");
            cart.setTotalPrice(BigDecimal.ZERO);
            cart.setCartItems(new LinkedHashSet<>());
            cartRepository.save(cart);
        }

        // Tìm sản phẩm
        Product product = productRepository.findById(request.getProductID())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // Tạo CartItem
        CartItem item = new CartItem();
        item.setCartID(cart);
        item.setProductID(product);
        item.setQuantity(request.getQuantity());
        item.setPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));

        // Cập nhật cart
        cart.getCartItems().add(item);
        cart.setTotalPrice(cart.getTotalPrice().add(item.getPrice()));

        cartRepository.save(cart);
        cartItemRepository.save(item);

        return mapCart(cart);
    }


    public CartResponse updateCartItemQuantity(int userId, CartItemUpdateRequest request) {
        Cart cart = cartRepository.findByUserID_IdAndStatus(userId, "active");
        if (cart == null) throw new AppException(ErrorCode.CART_NOT_FOUND);

        CartItem item = cart.getCartItems().stream()
                .filter(ci -> ci.getId().equals(request.getCartItemID()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_FOUND));

        Product product = item.getProductID();
        item.setQuantity(request.getQuantity());
        item.setPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
        cartItemRepository.save(item);

        recalculateTotalPrice(cart);
        cartRepository.save(cart);

        return mapCart(cart);
    }

    public CartResponse removeCartItem(int userId, int cartItemId) {
        Cart cart = cartRepository.findByUserID_IdAndStatus(userId, "active");
        if (cart == null) throw new AppException(ErrorCode.CART_NOT_FOUND);

        CartItem item = cart.getCartItems().stream()
                .filter(ci -> ci.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_FOUND));

        cart.getCartItems().remove(item);
        cartItemRepository.delete(item);

        recalculateTotalPrice(cart);
        cartRepository.save(cart);

        return mapCart(cart);
    }

    // ✅ Tính lại total price từ các cartItem
    private void recalculateTotalPrice(Cart cart) {
        BigDecimal total = cart.getCartItems().stream()
                .filter(ci -> ci.getPrice() != null)
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(total);
    }

    // ✅ Đảm bảo không có cartItem null trong response
    private CartResponse mapCart(Cart cart) {
        CartResponse res = cartMapper.toDto(cart);
        res.setCartItems(cartMapper.toCartItems(
                cart.getCartItems().stream()
                        .filter(item -> item != null && item.getProductID() != null)
                        .toList()
        ));
        return res;
    }
}
