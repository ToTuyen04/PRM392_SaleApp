package com.salesapp.service;

import com.salesapp.dto.request.ProductSearchRequest;
import com.salesapp.dto.response.ProductSearchResponse;
import com.salesapp.dto.response.ProductStatsResponse;
import com.salesapp.entity.Product;
import com.salesapp.repository.ProductRepository;
import com.salesapp.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSearchService {
    
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final EntityManager entityManager;
    
    public Page<ProductSearchResponse> searchProducts(ProductSearchRequest request) {
        // Tạo Pageable
        Pageable pageable = PageRequest.of(
            request.getPage() != null ? request.getPage() : 0,
            request.getSize() != null ? request.getSize() : 20
        );
        
        // Build dynamic query
        StringBuilder queryBuilder = new StringBuilder("SELECT p FROM Product p");
        StringBuilder countQueryBuilder = new StringBuilder("SELECT COUNT(p) FROM Product p");
        StringBuilder whereClause = new StringBuilder(" WHERE 1=1");
        
        List<Object> parameters = new ArrayList<>();
        int paramIndex = 1;
        
        // Filter by product name
        if (request.getProductName() != null && !request.getProductName().trim().isEmpty()) {
            whereClause.append(" AND LOWER(p.productName) LIKE LOWER(?").append(paramIndex).append(")");
            parameters.add("%" + request.getProductName().trim() + "%");
            paramIndex++;
        }
        
        // Filter by category
        if (request.getCategory() != null && !request.getCategory().trim().isEmpty()) {
            whereClause.append(" AND LOWER(p.categoryID.categoryName) LIKE LOWER(?").append(paramIndex).append(")");
            parameters.add("%" + request.getCategory().trim() + "%");
            paramIndex++;
        }
        
        // Filter by price range
        BigDecimal minPrice = request.getMinPrice();
        BigDecimal maxPrice = request.getMaxPrice();
        
        if (minPrice != null) {
            whereClause.append(" AND p.price >= ?").append(paramIndex);
            parameters.add(minPrice);
            paramIndex++;
        }
        
        if (maxPrice != null) {
            whereClause.append(" AND p.price <= ?").append(paramIndex);
            parameters.add(maxPrice);
            paramIndex++;
        }
        
        // Add WHERE clause to both queries
        queryBuilder.append(whereClause);
        countQueryBuilder.append(whereClause);
        
        // Add ORDER BY clause
        if ("price_asc".equals(request.getSortBy())) {
            queryBuilder.append(" ORDER BY p.price ASC");
        } else if ("price_desc".equals(request.getSortBy())) {
            queryBuilder.append(" ORDER BY p.price DESC");
        } else if ("name".equals(request.getSortBy())) {
            queryBuilder.append(" ORDER BY p.productName ASC");
        } else {
            queryBuilder.append(" ORDER BY p.id DESC"); // Default sorting
        }
        
        // Execute count query
        Query countQuery = entityManager.createQuery(countQueryBuilder.toString());
        for (int i = 0; i < parameters.size(); i++) {
            countQuery.setParameter(i + 1, parameters.get(i));
        }
        Long totalElements = (Long) countQuery.getSingleResult();
        
        // Execute main query
        Query query = entityManager.createQuery(queryBuilder.toString());
        for (int i = 0; i < parameters.size(); i++) {
            query.setParameter(i + 1, parameters.get(i));
        }
        
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        
        @SuppressWarnings("unchecked")
        List<Product> products = query.getResultList();
        
        // Convert to response DTOs
        List<ProductSearchResponse> responseList = products.stream()
            .map(ProductSearchResponse::fromProduct)
            .collect(Collectors.toList());
        
        // If sorting by most ordered, get order statistics and sort
        if ("most_ordered".equals(request.getSortBy())) {
            Map<Integer, Integer> orderStats = getProductOrderStatistics();
            
            responseList.forEach(response -> {
                response.setTotalOrdered(orderStats.getOrDefault(response.getId(), 0));
            });
            
            responseList.sort((a, b) -> Integer.compare(
                b.getTotalOrdered() != null ? b.getTotalOrdered() : 0,
                a.getTotalOrdered() != null ? a.getTotalOrdered() : 0
            ));
        }
        
        return new PageImpl<>(responseList, pageable, totalElements);
    }
    
    public List<ProductStatsResponse> getTopOrderedProducts(int limit) {
        // Query để lấy thống kê sản phẩm từ cart snapshots
        String sql = """
            SELECT 
                p.ProductID,
                p.ProductName,
                p.ImageURL,
                p.Price,
                c.CategoryName,
                COALESCE(stats.total_quantity, 0) as total_ordered,
                COALESCE(stats.order_count, 0) as order_count,
                COALESCE(stats.total_revenue, 0) as total_revenue
            FROM Products p
            LEFT JOIN Categories c ON p.CategoryID = c.CategoryID
            LEFT JOIN (
                SELECT 
                    product_id,
                    SUM(quantity) as total_quantity,
                    COUNT(*) as order_count,
                    SUM(subtotal) as total_revenue
                FROM (
                    SELECT 
                        JSON_EXTRACT(cart_item.value, '$.productId') as product_id,
                        CAST(JSON_EXTRACT(cart_item.value, '$.quantity') AS UNSIGNED) as quantity,
                        CAST(JSON_EXTRACT(cart_item.value, '$.subtotal') AS DECIMAL(18,2)) as subtotal
                    FROM Orders o
                    CROSS JOIN JSON_TABLE(
                        o.CartItemsSnapshot, 
                        '$[*]' COLUMNS (
                            value JSON PATH '$'
                        )
                    ) as cart_item
                    WHERE o.OrderStatus IN ('Processing', 'confirmed', 'delivered')
                ) as extracted_items
                GROUP BY product_id
            ) as stats ON p.ProductID = stats.product_id
            ORDER BY total_ordered DESC, total_revenue DESC
            LIMIT ?
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, limit);
        
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        
        return results.stream().map(row -> ProductStatsResponse.builder()
            .productId((Integer) row[0])
            .productName((String) row[1])
            .productImage((String) row[2])
            .price((BigDecimal) row[3])
            .category((String) row[4])
            .totalOrdered(((Number) row[5]).intValue())
            .orderCount(((Number) row[6]).intValue())
            .totalRevenue((BigDecimal) row[7])
            .build()
        ).collect(Collectors.toList());
    }
    
    private Map<Integer, Integer> getProductOrderStatistics() {
        // Query để tính thống kê đơn hàng từ cart snapshots
        String sql = """
            SELECT 
                product_id,
                SUM(quantity) as total_quantity
            FROM (
                SELECT 
                    JSON_EXTRACT(cart_item.value, '$.productId') as product_id,
                    CAST(JSON_EXTRACT(cart_item.value, '$.quantity') AS UNSIGNED) as quantity
                FROM Orders o
                CROSS JOIN JSON_TABLE(
                    o.CartItemsSnapshot, 
                    '$[*]' COLUMNS (
                        value JSON PATH '$'
                    )
                ) as cart_item
                WHERE o.OrderStatus IN ('Processing', 'confirmed', 'delivered')
            ) as extracted_items
            GROUP BY product_id
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        
        return results.stream().collect(Collectors.toMap(
            row -> (Integer) row[0],
            row -> ((Number) row[1]).intValue()
        ));
    }
}
