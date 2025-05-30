package com.xopix.productservice.services;

import com.xopix.productservice.dto.ProductRequestDTO;
import com.xopix.productservice.dto.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductResponseDTO> getAllProducts(String nameFilter, Pageable pageable);
    ProductResponseDTO getProductByProductId(Long productId);
    ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);
    ProductResponseDTO updateProduct(Long productId, ProductRequestDTO productRequestDTO);
    void deleteProduct(Long productId);
}
