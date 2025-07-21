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
}
