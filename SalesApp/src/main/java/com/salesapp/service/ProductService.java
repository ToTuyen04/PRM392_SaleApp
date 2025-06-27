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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService;

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

        // Delete image from Cloudinary if exists
        if (product.getImageURL() != null && !product.getImageURL().isEmpty()) {
            cloudinaryService.deleteImage(product.getImageURL());
        }

        productRepository.delete(product);
    }

    public List<ProductResponse> getProductsByCategoryId(Integer categoryId) {
        return productMapper.toDto(productRepository.findByCategoryID_Id(categoryId));
    }

    /**
     * Upload image for product
     * @param productId Product ID
     * @param file Image file to upload
     * @return Updated ProductResponse with new image URL
     */
    public ProductResponse uploadProductImage(Integer productId, MultipartFile file) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // Delete old image if exists
        if (product.getImageURL() != null && !product.getImageURL().isEmpty()) {
            cloudinaryService.deleteImage(product.getImageURL());
        }

        // Upload new image
        String imageUrl = cloudinaryService.uploadImage(file);
        product.setImageURL(imageUrl);

        productRepository.save(product);
        return productMapper.toDto(product);
    }

    /**
     * Delete image for product
     * @param productId Product ID
     * @return Updated ProductResponse without image
     */
    public ProductResponse deleteProductImage(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // Delete image from Cloudinary if exists
        if (product.getImageURL() != null && !product.getImageURL().isEmpty()) {
            cloudinaryService.deleteImage(product.getImageURL());
            product.setImageURL(null);
            productRepository.save(product);
        }

        return productMapper.toDto(product);
    }
}
