package com.salesapp.controller.v1;

import com.salesapp.dto.request.CategoryCreateRequest;
import com.salesapp.dto.response.CategoryResponse;
import com.salesapp.dto.response.ResponseObject;
import com.salesapp.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Category", description = "Manage Categories")
public class CategoryV1Controller {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseObject<List<CategoryResponse>> getAllCategories() {
        return ResponseObject.<List<CategoryResponse>>builder()
                .status(1000)
                .message("All categories fetched")
                .data(categoryService.getAll())
                .build();
    }

    @GetMapping("/{id}")
    public ResponseObject<CategoryResponse> getCategoryById(@PathVariable int id) {
        return ResponseObject.<CategoryResponse>builder()
                .status(1000)
                .message("Category fetched")
                .data(categoryService.getById(id))
                .build();
    }

    @PostMapping
    public ResponseObject<CategoryResponse> createCategory(@RequestBody CategoryCreateRequest request) {
        return ResponseObject.<CategoryResponse>builder()
                .status(1000)
                .message("Category created")
                .data(categoryService.create(request))
                .build();
    }

    @PutMapping("/{id}")
    public ResponseObject<CategoryResponse> updateCategory(@PathVariable int id, @RequestBody CategoryCreateRequest request) {
        return ResponseObject.<CategoryResponse>builder()
                .status(1000)
                .message("Category updated")
                .data(categoryService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseObject<Void> deleteCategory(@PathVariable int id) {
        categoryService.delete(id);
        return ResponseObject.<Void>builder()
                .status(1000)
                .message("Category deleted")
                .data(null)
                .build();
    }
}
