package com.salesapp.service;


import com.salesapp.dto.request.CategoryCreateRequest;
import com.salesapp.dto.request.UserCreateRequest;
import com.salesapp.dto.response.CategoryResponse;
import com.salesapp.dto.response.UserResponse;
import com.salesapp.entity.Category;
import com.salesapp.entity.User;
import com.salesapp.exception.AppException;
import com.salesapp.exception.ErrorCode;
import com.salesapp.mapper.CategoryMapper;
import com.salesapp.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CategoryMapper categoryMapper;

    public List<CategoryResponse> getAll() {
        return categoryMapper.toDto(categoryRepository.findAll());
    }

    public CategoryResponse getById(int id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        return categoryMapper.toDto(category);
    }

    public CategoryResponse create(CategoryCreateRequest request) {
        if (categoryRepository.existsByCategoryName(request.getCategoryName())) {
            throw new AppException(ErrorCode.CATEGORY_NAME_EXIST);
        }
        Category category = categoryMapper.toCategory(request);
        categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    public CategoryResponse update(int id, CategoryCreateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        categoryMapper.updateCategory(category, request);
        categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    public void delete(int id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        categoryRepository.delete(category);
    }
}
