package com.xopix.productservice.services;

import com.xopix.productservice.dto.ProductRequestDTO;
import com.xopix.productservice.dto.ProductResponseDTO;
import com.xopix.productservice.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<Product> getAllProducts(int page, int size, String sortBy, String sortDir);
    Product getProductByProductId(String  productId);
    Product createProduct(ProductRequestDTO productRequestDTO);
    Product updateProduct(String  productId, ProductRequestDTO productRequestDTO);
    void deleteProduct(String  productId);
}
