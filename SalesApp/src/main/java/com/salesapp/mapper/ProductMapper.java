package com.salesapp.mapper;

import com.salesapp.dto.request.ProductRequest;
import com.salesapp.dto.response.ProductResponse;
import com.salesapp.entity.Category;
import com.salesapp.entity.Product;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {
    @Autowired
    protected CategoryMapperSupport categoryMapperSupport;

    public abstract List<ProductResponse> toDto(List<Product> products);

    @Mapping(source = "categoryID", target = "categoryID")
    @Mapping(source = "categoryID.categoryName", target = "categoryName")
    public abstract ProductResponse toDto(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoryID", expression = "java(categoryMapperSupport.mapCategoryId(request.getCategoryID()))")
    public abstract Product toProduct(ProductRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "categoryID", expression = "java(categoryMapperSupport.mapCategoryId(request.getCategoryID()))")
    public abstract void updateProduct(@MappingTarget Product product, ProductRequest request);

    protected Integer map(Category category) {
        return category != null ? category.getId() : null;
    }

    @Component
    public static class CategoryMapperSupport {
        @Autowired
        private com.salesapp.repository.CategoryRepository categoryRepository;

        public Category mapCategoryId(Integer categoryId) {
            return categoryRepository.findById(categoryId).orElse(null);
        }
    }
}
