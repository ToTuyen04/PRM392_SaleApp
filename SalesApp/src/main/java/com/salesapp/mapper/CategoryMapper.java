package com.salesapp.mapper;


import com.salesapp.dto.request.CategoryCreateRequest;
import com.salesapp.dto.request.UserCreateRequest;
import com.salesapp.dto.request.UserUpdateRequest;
import com.salesapp.dto.response.CategoryResponse;
import com.salesapp.dto.response.UserResponse;
import com.salesapp.entity.Category;
import com.salesapp.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toDto(Category category);
    List<CategoryResponse> toDto(List<Category> categories);
    Category toCategory(CategoryCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCategory(@MappingTarget Category category, CategoryCreateRequest request);
}
