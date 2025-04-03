package com.simplesdental.product.controller;

import com.simplesdental.product.model.ProductV2;
import com.simplesdental.product.service.ProductV2Service;
import com.simplesdental.product.util.LogUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v2/products")
@Tag(name = "Produtos V2", description = "API para gerenciamento de produtos (versão 2)")
@SecurityRequirement(name = "bearer-key")
public class ProductV2Controller {

    private final ProductV2Service productService;
    private static final String CLASS_NAME = ProductV2Controller.class.getName();

    public ProductV2Controller(ProductV2Service productService) {
        this.productService = productService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "List all products", description = "Returns a paginated list of all products")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
        @ApiResponse(responseCode = "403", description = "Access forbidden"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<Page<ProductV2>> getAllProducts(Pageable pageable) {
        Map<String, Object> logContext = new HashMap<>();
        logContext.put("page", pageable.getPageNumber());
        logContext.put("size", pageable.getPageSize());
        
        LogUtils.logDebug("Getting all products with pagination", CLASS_NAME, logContext);
        Page<ProductV2> products = productService.getAllProducts(pageable);
        
        logContext.put("totalElements", products.getTotalElements());
        LogUtils.logInfo("Successfully retrieved products", CLASS_NAME, logContext);
        
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get a product by ID", description = "Returns a single product by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved product"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "403", description = "Access forbidden"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<ProductV2> getProduct(@PathVariable Long id) {
        Map<String, Object> logContext = new HashMap<>();
        logContext.put("productId", id);
        
        LogUtils.logDebug("Getting product by ID", CLASS_NAME, logContext);
        try {
            ProductV2 product = productService.getProduct(id);
            LogUtils.logInfo("Successfully retrieved product", CLASS_NAME, logContext);
            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e) {
            LogUtils.logWarning("Product not found", CLASS_NAME, logContext);
            throw e;
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new product", description = "Creates a new product and returns it")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created product"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Access forbidden"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<ProductV2> createProduct(@Valid @RequestBody ProductV2 product) {
        Map<String, Object> logContext = new HashMap<>();
        logContext.put("productName", product.getName());
        logContext.put("categoryId", product.getCategory() != null ? product.getCategory().getId() : null);
        
        LogUtils.logDebug("Creating new product", CLASS_NAME, logContext);
        ProductV2 createdProduct = productService.createProduct(product);
        
        logContext.put("productId", createdProduct.getId());
        LogUtils.logInfo("Successfully created product", CLASS_NAME, logContext);
        
        return ResponseEntity.ok(createdProduct);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a product", description = "Updates an existing product by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated product"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Access forbidden"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<ProductV2> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductV2 product) {
        Map<String, Object> logContext = new HashMap<>();
        logContext.put("productId", id);
        logContext.put("productName", product.getName());
        
        LogUtils.logDebug("Updating product", CLASS_NAME, logContext);
        try {
            ProductV2 updatedProduct = productService.updateProduct(id, product);
            LogUtils.logInfo("Successfully updated product", CLASS_NAME, logContext);
            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException e) {
            LogUtils.logWarning("Product not found for update", CLASS_NAME, logContext);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a product", description = "Deletes a product by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully deleted product"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "403", description = "Access forbidden"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Map<String, Object> logContext = new HashMap<>();
        logContext.put("productId", id);
        
        LogUtils.logDebug("Deleting product", CLASS_NAME, logContext);
        try {
            productService.deleteProduct(id);
            LogUtils.logInfo("Successfully deleted product", CLASS_NAME, logContext);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            LogUtils.logWarning("Product not found for deletion", CLASS_NAME, logContext);
            throw e;
        }
    }
}
