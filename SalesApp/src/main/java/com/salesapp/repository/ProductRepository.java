package com.salesapp.repository;

import com.salesapp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    boolean existsByProductName(String productName);
    List<Product> findByCategoryID_Id(Integer categoryId);
}