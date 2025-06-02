package com.xopix.productservice.controllers;


import com.xopix.productservice.dto.ProductRequestDTO;
import com.xopix.productservice.dto.ProductResponseDTO;
import com.xopix.productservice.models.Product;
import com.xopix.productservice.services.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;


    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Fetching products with page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        Page<Product> productsPage = productService.getAllProducts(page, size, sortBy, sortDir);
        List<ProductResponseDTO> productResponses = productsPage.getContent().stream()
                .map(ProductResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productResponses);
    }

    @RequestMapping(value = "/{productId}", method = RequestMethod.GET)
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable String productId) {
        log.info("Fetching product with ID: {}", productId);
        Product product = productService.getProductByProductId(productId);
        return ResponseEntity.ok(ProductResponseDTO.fromEntity(product));
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    // Enforce ADMIN role
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO request) {
        log.info("Received request to create product with SKU: {}", request.getSku());
        Product newProduct = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponseDTO.fromEntity(newProduct));
    }

    @RequestMapping(value = "/{productId}", method = RequestMethod.PUT)
    // Enforce ADMIN role
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable String productId,
                                                         @Valid @RequestBody ProductRequestDTO request) {
        log.info("Received request to update product with ID: {}", productId);
        Product updatedProduct = productService.updateProduct(productId, request);
        return ResponseEntity.ok(ProductResponseDTO.fromEntity(updatedProduct));
    }

    @RequestMapping(value = "/{productId}", method = RequestMethod.DELETE)
    // Enforce ADMIN role
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable String productId) {
        log.info("Received request to delete product with ID: {}", productId);
        productService.deleteProduct(productId);
    }
}
