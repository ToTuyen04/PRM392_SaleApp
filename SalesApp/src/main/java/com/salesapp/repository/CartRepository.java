package com.salesapp.repository;

import com.salesapp.entity.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    // Lấy tất cả cart của user
    List<Cart> findByUserID_Id(Integer userId);

    // Lấy cart active (chưa cần fetch cartItems)
    Cart findByUserID_IdAndStatus(Integer userId, String status);

    // ✅ Lấy cart theo ID, luôn load cartItems và product
    @EntityGraph(attributePaths = {"cartItems", "cartItems.productID"})
    Optional<Cart> findById(Integer cartId);
}

