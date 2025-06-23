package com.salesapp.repository;

import com.salesapp.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findByUserID_Id(Integer userId);
    Cart findByUserID_IdAndStatus(Integer userId, String status);
}
