package com.salesapp.controller.v1;

import com.salesapp.dto.request.ProductRequest;
import com.salesapp.dto.response.ProductResponse;
import com.salesapp.dto.response.ResponseObject;
import com.salesapp.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product", description = "Manage Products")
public class ProductV1Controller {

    private final ProductService productService;

    @GetMapping
    public ResponseObject<List<ProductResponse>> getAll() {
        return ResponseObject.<List<ProductResponse>>builder()
                .status(1000)
                .message("All products fetched")
                .data(productService.getAllProducts())
                .build();
    }

    @GetMapping("/{id}")
    public ResponseObject<ProductResponse> getById(@PathVariable Integer id) {
        return ResponseObject.<ProductResponse>builder()
                .status(1000)
                .message("Product fetched")
                .data(productService.getProductById(id))
                .build();
    }

    @GetMapping("/category/{categoryId}")
    public ResponseObject<List<ProductResponse>> getByCategory(@PathVariable Integer categoryId) {
        return ResponseObject.<List<ProductResponse>>builder()
                .status(1000)
                .message("Products by category fetched")
                .data(productService.getProductsByCategoryId(categoryId))
                .build();
    }

    @PostMapping
    public ResponseObject<ProductResponse> create(@RequestBody ProductRequest request) {
        return ResponseObject.<ProductResponse>builder()
                .status(1000)
                .message("Product created")
                .data(productService.createProduct(request))
                .build();
    }

    @PutMapping("/{id}")
    public ResponseObject<ProductResponse> update(@PathVariable Integer id, @RequestBody ProductRequest request) {
        return ResponseObject.<ProductResponse>builder()
                .status(1000)
                .message("Product updated")
                .data(productService.updateProduct(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseObject<Void> delete(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseObject.<Void>builder()
                .status(1000)
                .message("Product deleted")
                .data(null)
                .build();
    }
}
