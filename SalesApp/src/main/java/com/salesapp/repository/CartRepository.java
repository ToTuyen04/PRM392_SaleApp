package com.salesapp.repository;

import com.salesapp.entity.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    // Lấy cart active mới nhất (tránh lỗi multiple results)
    @EntityGraph(attributePaths = {"cartItems", "cartItems.productID"})
    Optional<Cart> findFirstByUserID_IdAndStatusOrderByIdDesc(Integer userId, String status);

    // Lấy tất cả cart của user
    List<Cart> findByUserID_Id(Integer userId);

    // Lấy cart theo ID với cartItems
    @EntityGraph(attributePaths = {"cartItems", "cartItems.productID"})
    Optional<Cart> findById(Integer cartId);
}

