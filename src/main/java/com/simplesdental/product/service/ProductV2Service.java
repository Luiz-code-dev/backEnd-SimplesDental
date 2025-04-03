package com.simplesdental.product.service;

import com.simplesdental.product.model.ProductV2;
import com.simplesdental.product.repository.ProductV2Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductV2Service {

    private final ProductV2Repository productRepository;

    public ProductV2Service(ProductV2Repository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<ProductV2> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public ProductV2 getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
    }

    @Transactional
    public ProductV2 createProduct(ProductV2 product) {
        return productRepository.save(product);
    }

    @Transactional
    public ProductV2 updateProduct(Long id, ProductV2 product) {
        if (!productRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }
        product.setId(id);
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        ProductV2 product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        productRepository.delete(product);
    }
}
