package com.salesapp.mapper;

import com.salesapp.dto.request.ProductRequest;
import com.salesapp.dto.response.ProductResponse;
import com.salesapp.entity.Category;
import com.salesapp.entity.Product;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "categoryID.id", target = "categoryID")
    @Mapping(source = "categoryID.categoryName", target = "categoryName")
    ProductResponse toDto(Product product);

    List<ProductResponse> toDto(List<Product> products);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoryID", expression = "java(categoryMapperSupport.mapCategoryId(request.getCategoryID()))")
    Product toProduct(ProductRequest request, @Context CategoryMapperSupport categoryMapperSupport);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "categoryID", expression = "java(categoryMapperSupport.mapCategoryId(request.getCategoryID()))")
    void updateProduct(@MappingTarget Product product, ProductRequest request, @Context CategoryMapperSupport categoryMapperSupport);

    // Ánh xạ để hiển thị ID ra response
    default Integer mapCategoryToId(Category category) {
        return category != null ? category.getId() : null;
    }

    // Context Helper
    class CategoryMapperSupport {
        private final com.salesapp.repository.CategoryRepository categoryRepository;

        public CategoryMapperSupport(com.salesapp.repository.CategoryRepository categoryRepository) {
            this.categoryRepository = categoryRepository;
        }

        public Category mapCategoryId(Integer categoryId) {
            return categoryRepository.findById(categoryId).orElse(null);
        }
    }
}
