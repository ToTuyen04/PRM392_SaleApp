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
public class ProductStatsResponse {
    private Integer productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private String category;
    private Integer totalOrdered; // Tổng số lượng đã được đặt hàng
    private Integer orderCount; // Số lần được đặt hàng
    private BigDecimal totalRevenue; // Tổng doanh thu từ sản phẩm này
}
