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
import java.util.*;
import java.util.stream.Collectors;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    public CartResponse getActiveCart(int userId) {
        Optional<Cart> cartOpt = cartRepository.findFirstByUserID_IdAndStatusOrderByIdDesc(userId, "active");

        Cart cart;
        if (cartOpt.isEmpty()) {
            // Tạo cart mới nếu chưa có
            cart = new Cart();
            cart.setUserID(userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND)));
            cart.setStatus("active");
            cart.setTotalPrice(BigDecimal.ZERO);
            cart.setCartItems(new LinkedHashSet<>());
            cart = cartRepository.save(cart);
        } else {
            cart = cartOpt.get();
        }

        return mapCart(cart);
    }

    public CartResponse addToCart(int userId, CartItemRequest request) {
        // Tìm cart active của user, nếu chưa có thì tạo mới
        Optional<Cart> cartOpt = cartRepository.findFirstByUserID_IdAndStatusOrderByIdDesc(userId, "active");

        Cart cart;
        if (cartOpt.isEmpty()) {
            cart = new Cart();
            cart.setUserID(userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND)));
            cart.setStatus("active");
            cart.setTotalPrice(BigDecimal.ZERO);
            cart.setCartItems(new LinkedHashSet<>());
            cart = cartRepository.save(cart); // Lưu lần đầu để lấy cartID
        } else {
            cart = cartOpt.get();
        }

        // Tìm sản phẩm từ productID
        Product product = productRepository.findById(request.getProductID())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        CartItem existingItem = cartItemRepository.findByCartID_IdAndProductID_Id(cart.getId(), request.getProductID());

        if (existingItem != null) {
            // Nếu sản phẩm đã có, cập nhật quantity và price
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            BigDecimal oldPrice = existingItem.getPrice();
            BigDecimal newPrice = product.getPrice().multiply(BigDecimal.valueOf(newQuantity));

            existingItem.setQuantity(newQuantity);
            existingItem.setPrice(newPrice);
            cartItemRepository.save(existingItem);

            // Cập nhật tổng tiền cart (trừ giá cũ, cộng giá mới)
            cart.setTotalPrice(cart.getTotalPrice().subtract(oldPrice).add(newPrice));
            cartRepository.save(cart);
        } else {
            // Nếu sản phẩm chưa có, tạo mới CartItem
            CartItem item = new CartItem();
            item.setCartID(cart);
            item.setProductID(product);
            item.setQuantity(request.getQuantity());
            item.setPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));

            // Lưu CartItem trước
            cartItemRepository.save(item);
            cart.getCartItems().add(item);

            // Cập nhật lại tổng tiền cho cart
            cart.setTotalPrice(cart.getTotalPrice().add(item.getPrice()));
            cartRepository.save(cart);
        }

        // Quan trọng: Tải lại cart từ DB để đảm bảo có đầy đủ cartItems
        Cart updatedCart = cartRepository.findById(cart.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        return mapCart(updatedCart);
    }

    public CartResponse updateCartItemQuantity(int userId, CartItemUpdateRequest request) {
        Optional<Cart> cartOpt = cartRepository.findFirstByUserID_IdAndStatusOrderByIdDesc(userId, "active");
        if (cartOpt.isEmpty()) throw new AppException(ErrorCode.CART_NOT_FOUND);

        Cart cart = cartOpt.get();

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

        // Tải lại cart từ DB để đảm bảo có dữ liệu mới nhất
        Cart updatedCart = cartRepository.findById(cart.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        return mapCart(updatedCart);
    }

    public CartResponse removeCartItem(int userId, int cartItemId) {
        Optional<Cart> cartOpt = cartRepository.findFirstByUserID_IdAndStatusOrderByIdDesc(userId, "active");
        if (cartOpt.isEmpty()) throw new AppException(ErrorCode.CART_NOT_FOUND);

        Cart cart = cartOpt.get();

        CartItem item = cart.getCartItems().stream()
                .filter(ci -> ci.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_FOUND));

        cart.getCartItems().remove(item);
        cartItemRepository.delete(item);

        recalculateTotalPrice(cart);
        cartRepository.save(cart);

        // Tải lại cart từ DB để đảm bảo có dữ liệu mới nhất
        Cart updatedCart = cartRepository.findById(cart.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        return mapCart(updatedCart);
    }

    // Tính lại total price từ các cartItem
    private void recalculateTotalPrice(Cart cart) {
        BigDecimal total = cart.getCartItems().stream()
                .filter(ci -> ci.getPrice() != null)
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(total);
    }

    // Đảm bảo không có cartItem null trong response và sắp xếp theo cartItemId
    private CartResponse mapCart(Cart cart) {
        CartResponse res = cartMapper.toDto(cart);

        // Sắp xếp cart items theo ID tăng dần để đảm bảo thứ tự nhất quán
        List<CartItem> sortedItems = cart.getCartItems().stream()
                .filter(item -> item != null && item.getProductID() != null)
                .sorted(Comparator.comparing(CartItem::getId)) // Sắp xếp theo cartItemId tăng dần
                .toList();

        res.setCartItems(cartMapper.toCartItems(sortedItems));
        return res;
    }

    // Method để dọn dẹp cart - gộp các CartItem trùng lặp
    public CartResponse cleanupDuplicateCartItems(int userId) {
        Optional<Cart> cartOpt = cartRepository.findFirstByUserID_IdAndStatusOrderByIdDesc(userId, "active");
        if (cartOpt.isEmpty()) {
            throw new AppException(ErrorCode.CART_NOT_FOUND);
        }

        Cart cart = cartOpt.get();

        // Lấy tất cả CartItem trong cart
        Set<CartItem> cartItems = cart.getCartItems();
        Map<Integer, CartItem> productMap = new HashMap<>();
        List<CartItem> itemsToDelete = new ArrayList<>();

        // Gộp các item trùng lặp
        for (CartItem item : cartItems) {
            Integer productId = item.getProductID().getId();

            if (productMap.containsKey(productId)) {
                // Nếu đã có sản phẩm này, cộng dồn quantity
                CartItem existingItem = productMap.get(productId);
                int newQuantity = existingItem.getQuantity() + item.getQuantity();
                BigDecimal newPrice = existingItem.getProductID().getPrice()
                        .multiply(BigDecimal.valueOf(newQuantity));

                existingItem.setQuantity(newQuantity);
                existingItem.setPrice(newPrice);

                // Đánh dấu item trùng lặp để xóa
                itemsToDelete.add(item);
            } else {
                // Nếu chưa có, thêm vào map
                productMap.put(productId, item);
            }
        }

        // Xóa các item trùng lặp
        for (CartItem itemToDelete : itemsToDelete) {
            cartItemRepository.delete(itemToDelete);
            cart.getCartItems().remove(itemToDelete);
        }

        // Lưu các item đã được cập nhật
        for (CartItem item : productMap.values()) {
            cartItemRepository.save(item);
        }

        // Tính lại tổng tiền
        BigDecimal newTotalPrice = productMap.values().stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalPrice(newTotalPrice);
        cartRepository.save(cart);

        // Tải lại cart từ DB
        Cart updatedCart = cartRepository.findById(cart.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        return mapCart(updatedCart);
    }
    public CartResponse clearCart(int userId) {
        Optional<Cart> cartOpt = cartRepository.findFirstByUserID_IdAndStatusOrderByIdDesc(userId, "active");
        if (cartOpt.isEmpty()) throw new AppException(ErrorCode.CART_NOT_FOUND);

        Cart cart = cartOpt.get();

        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
        return mapCart(cart);
    }

}
