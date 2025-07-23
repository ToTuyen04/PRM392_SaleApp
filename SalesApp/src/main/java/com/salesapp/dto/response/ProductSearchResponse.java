package com.salesapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchResponse {
    private Integer id;
    private String productName;
    private String productImage; // URL của hình ảnh
    private BigDecimal price;
    private String category; // Tên category
    private String briefDescription;
    private String fullDescription;
    private Integer totalOrdered; // Tổng số lượng đã được đặt hàng (cho sorting)
    
    // Constructor từ Product entity
    public static ProductSearchResponse fromProduct(com.salesapp.entity.Product product) {
        return ProductSearchResponse.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .productImage(product.getImageURL())
                .price(product.getPrice())
                .category(product.getCategoryID() != null ? product.getCategoryID().getCategoryName() : "Unknown")
                .briefDescription(product.getBriefDescription())
                .fullDescription(product.getFullDescription())
                .totalOrdered(0) // Sẽ được set sau khi tính toán
                .build();
    }
}
