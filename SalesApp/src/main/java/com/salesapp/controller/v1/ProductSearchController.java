package com.salesapp.controller.v1;

import com.salesapp.dto.request.ProductSearchRequest;
import com.salesapp.dto.response.ProductSearchResponse;
import com.salesapp.dto.response.ProductStatsResponse;
import com.salesapp.service.ProductSearchService;
import com.salesapp.dto.response.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Search", description = "API for searching and filtering products")
public class ProductSearchController {
    
    private final ProductSearchService productSearchService;
    
    @GetMapping("/search")
    @Operation(summary = "Search and filter products", 
               description = "Search products by name, filter by category, price range, and sort by various criteria")
    public ResponseEntity<ResponseObject<Page<ProductSearchResponse>>> searchProducts(
            @Parameter(description = "Product name to search for") 
            @RequestParam(required = false) String productName,
            
            @Parameter(description = "Category to filter by") 
            @RequestParam(required = false) String category,
            
            @Parameter(description = "Price range filter", 
                      example = "under_1m, 1m_to_10m, over_10m") 
            @RequestParam(required = false) String priceRange,
            
            @Parameter(description = "Sort criteria", 
                      example = "name, price_asc, price_desc, most_ordered") 
            @RequestParam(defaultValue = "name") String sortBy,
            
            @Parameter(description = "Page number (0-based)") 
            @RequestParam(defaultValue = "0") Integer page,
            
            @Parameter(description = "Page size") 
            @RequestParam(defaultValue = "20") Integer size) {
        
        ProductSearchRequest request = ProductSearchRequest.builder()
                .productName(productName)
                .category(category)
                .priceRange(priceRange)
                .sortBy(sortBy)
                .page(page)
                .size(size)
                .build();
        
        Page<ProductSearchResponse> result = productSearchService.searchProducts(request);
        
        return ResponseEntity.ok(ResponseObject.<Page<ProductSearchResponse>>builder()
                .status(1000)
                .message("Products retrieved successfully")
                .data(result)
                .build());
    }
    
    @GetMapping("/stats/most-ordered")
    @Operation(summary = "Get most ordered products statistics", 
               description = "Get statistics of products ordered most frequently based on order snapshots")
    public ResponseEntity<ResponseObject<List<ProductStatsResponse>>> getMostOrderedProducts(
            @Parameter(description = "Number of top products to return") 
            @RequestParam(defaultValue = "10") Integer limit) {
        
        List<ProductStatsResponse> result = productSearchService.getTopOrderedProducts(limit);
        
        return ResponseEntity.ok(ResponseObject.<List<ProductStatsResponse>>builder()
                .status(1000)
                .message("Most ordered products retrieved successfully")
                .data(result)
                .build());
    }
    
    @GetMapping("/filter-options")
    @Operation(summary = "Get available filter options", 
               description = "Get all available categories and price ranges for filtering")
    public ResponseEntity<ResponseObject<FilterOptionsResponse>> getFilterOptions() {
        
        FilterOptionsResponse options = FilterOptionsResponse.builder()
                .priceRanges(List.of(
                    new PriceRangeOption("under_1m", "Dưới 1.000.000đ", "0", "999999"),
                    new PriceRangeOption("1m_to_10m", "1.000.000đ - 10.000.000đ", "1000000", "10000000"),
                    new PriceRangeOption("over_10m", "Trên 10.000.000đ", "10000000", null)
                ))
                .sortOptions(List.of(
                    new SortOption("name", "Tên sản phẩm (A-Z)"),
                    new SortOption("price_asc", "Giá thấp đến cao"),
                    new SortOption("price_desc", "Giá cao đến thấp"),
                    new SortOption("most_ordered", "Bán chạy nhất")
                ))
                .build();
        
        return ResponseEntity.ok(ResponseObject.<FilterOptionsResponse>builder()
                .status(1000)
                .message("Filter options retrieved successfully")
                .data(options)
                .build());
    }
    
    // Inner classes for filter options
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class FilterOptionsResponse {
        private List<PriceRangeOption> priceRanges;
        private List<SortOption> sortOptions;
    }
    
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class PriceRangeOption {
        private String value;
        private String label;
        private String minPrice;
        private String maxPrice;
    }
    
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class SortOption {
        private String value;
        private String label;
    }
}
