package com.salesapp.repository;

import com.salesapp.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByUserID_IdOrderByIdDesc(int userId);

    @EntityGraph(attributePaths = {"payments"})
    Optional<Order> findWithPaymentsById(Integer id);
    
    @EntityGraph(attributePaths = {"cartID.cartItems.productID", "payments"})
    List<Order> findWithCartItemsByUserID_IdOrderByIdDesc(int userId);
    
    @EntityGraph(attributePaths = {"cartID.cartItems.productID", "payments"})
    Optional<Order> findWithCartItemsById(Integer id);
    
    // Lấy tất cả orders, sắp xếp theo ID giảm dần (admin/staff function)
    @EntityGraph(attributePaths = {"cartID.cartItems.productID", "payments"})
    List<Order> findAllByOrderByIdDesc();
}
