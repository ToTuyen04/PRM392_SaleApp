package com.salesapp.controller.v1;

import com.salesapp.dto.request.CategoryCreateRequest;
import com.salesapp.dto.response.CategoryResponse;
import com.salesapp.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAll();
    }

    @GetMapping("/{id}")
    public CategoryResponse getCategoryById(@PathVariable int id) {
        return categoryService.getById(id);
    }

    @PostMapping
    public CategoryResponse createCategory(@RequestBody CategoryCreateRequest request) {
        return categoryService.create(request);
    }

    @PutMapping("/{id}")
    public CategoryResponse updateCategory(@PathVariable int id, @RequestBody CategoryCreateRequest request) {
        return categoryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable int id) {
        categoryService.delete(id);
    }
}