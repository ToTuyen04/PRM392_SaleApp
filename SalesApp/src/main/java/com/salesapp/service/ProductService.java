package com.salesapp.service;

import com.salesapp.dto.request.ProductRequest;
import com.salesapp.dto.response.ProductResponse;
import com.salesapp.entity.Product;
import com.salesapp.exception.AppException;
import com.salesapp.exception.ErrorCode;
import com.salesapp.mapper.ProductMapper;
import com.salesapp.mapper.ProductMapper.CategoryMapperSupport;
import com.salesapp.repository.CategoryRepository;
import com.salesapp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    public List<ProductResponse> getAllProducts() {
        return productMapper.toDto(productRepository.findAll());
    }

    public ProductResponse getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return productMapper.toDto(product);
    }

    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsByProductName(request.getProductName())) {
            throw new AppException(ErrorCode.PRODUCT_NAME_EXIST);
        }

        // Sử dụng CategoryMapperSupport
        CategoryMapperSupport support = new CategoryMapperSupport(categoryRepository);
        Product product = productMapper.toProduct(request, support);
        productRepository.save(product);
        return productMapper.toDto(product);
    }

    public ProductResponse updateProduct(Integer id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        CategoryMapperSupport support = new CategoryMapperSupport(categoryRepository);
        productMapper.updateProduct(product, request, support);
        productRepository.save(product);
        return productMapper.toDto(product);
    }

    public void deleteProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        productRepository.delete(product);
    }

    public List<ProductResponse> getProductsByCategoryId(Integer categoryId) {
        return productMapper.toDto(productRepository.findByCategoryID_Id(categoryId));
    }
}
