package com.salesapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchRequest {
    private String productName; // Tìm kiếm theo tên sản phẩm
    private String category; // Filter theo category
    private String priceRange; // "under_1m", "1m_to_10m", "over_10m"
    private String sortBy; // "name", "price_asc", "price_desc", "most_ordered"
    private Integer page; // Phân trang
    private Integer size; // Số lượng sản phẩm mỗi trang
    
    // Helper methods to get price range
    public BigDecimal getMinPrice() {
        if (priceRange == null) return null;
        switch (priceRange) {
            case "under_1m":
                return BigDecimal.ZERO;
            case "1m_to_10m":
                return new BigDecimal("1000000");
            case "over_10m":
                return new BigDecimal("10000000");
            default:
                return null;
        }
    }
    
    public BigDecimal getMaxPrice() {
        if (priceRange == null) return null;
        switch (priceRange) {
            case "under_1m":
                return new BigDecimal("999999");
            case "1m_to_10m":
                return new BigDecimal("10000000");
            case "over_10m":
                return null; // Không giới hạn trên
            default:
                return null;
        }
    }
}
