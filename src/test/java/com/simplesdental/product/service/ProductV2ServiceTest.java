package com.simplesdental.product.service;

import com.simplesdental.product.model.Category;
import com.simplesdental.product.model.ProductV2;
import com.simplesdental.product.repository.ProductV2Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductV2ServiceTest {

    @Mock
    private ProductV2Repository productRepository;

    @InjectMocks
    private ProductV2Service productService;

    private ProductV2 product;
    private Page<ProductV2> productPage;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        product = new ProductV2();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(BigDecimal.TEN);
        product.setCategory(category);
        product.setCode(123);

        productPage = new PageImpl<>(List.of(product));
    }

    @Test
    void getAllProducts_ShouldReturnPageOfProducts() {
        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);

        Page<ProductV2> result = productService.getAllProducts(Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(product.getName(), result.getContent().get(0).getName());
        assertEquals(product.getCode(), result.getContent().get(0).getCode());
    }

    @Test
    void getProduct_WithValidId_ShouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductV2 result = productService.getProduct(1L);

        assertNotNull(result);
        assertEquals(product.getId(), result.getId());
        assertEquals(product.getName(), result.getName());
        assertEquals(product.getCode(), result.getCode());
    }

    @Test
    void getProduct_WithInvalidId_ShouldThrowException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productService.getProduct(1L));
    }

    @Test
    void createProduct_WithValidData_ShouldReturnCreatedProduct() {
        when(productRepository.save(any(ProductV2.class))).thenReturn(product);

        ProductV2 result = productService.createProduct(product);

        assertNotNull(result);
        assertEquals(product.getName(), result.getName());
        assertEquals(product.getCode(), result.getCode());
        verify(productRepository).save(any(ProductV2.class));
    }

    @Test
    void updateProduct_WithValidData_ShouldReturnUpdatedProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(ProductV2.class))).thenReturn(product);

        ProductV2 result = productService.updateProduct(1L, product);

        assertNotNull(result);
        assertEquals(product.getName(), result.getName());
        assertEquals(product.getCode(), result.getCode());
        verify(productRepository).save(any(ProductV2.class));
    }

    @Test
    void updateProduct_WithInvalidId_ShouldThrowException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(1L, product));
        verify(productRepository, never()).save(any(ProductV2.class));
    }

    @Test
    void deleteProduct_WithValidId_ShouldDeleteProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(any(ProductV2.class));

        productService.deleteProduct(1L);

        verify(productRepository).delete(product);
    }

    @Test
    void deleteProduct_WithInvalidId_ShouldThrowException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productService.deleteProduct(1L));
        verify(productRepository, never()).delete(any(ProductV2.class));
    }
}
