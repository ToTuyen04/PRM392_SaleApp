package com.salesapp.repository;

import com.salesapp.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    // Tìm CartItem theo cartId và productId
    CartItem findByCartID_IdAndProductID_Id(Integer cartId, Integer productId);

    // Xóa tất cả CartItem theo cartId
    void deleteByCartID_Id(Integer cartId);
}
