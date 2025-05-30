package com.xopix.productservice.controllers;


import com.xopix.productservice.dto.ProductRequestDTO;
import com.xopix.productservice.dto.ProductResponseDTO;
import com.xopix.productservice.models.Product;
import com.xopix.productservice.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;


    @RequestMapping(value = "", method = RequestMethod.GET)
    public Page<ProductResponseDTO> getAllProducts(
            @RequestParam String name,
            @PageableDefault(size = 10, sort = "productId")Pageable pageable
            ) {
        return productService.getAllProducts(name, pageable);
    }

    @RequestMapping(value = "/{productId}", method = RequestMethod.GET)
    public ProductResponseDTO getProductById(@PathVariable Long productId) {
        return productService.getProductByProductId(productId);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        return new ResponseEntity<>(productService.createProduct(productRequestDTO), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{productId}", method = RequestMethod.PUT)
    public ProductResponseDTO updateProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO,
                                            @PathVariable Long productId) {
        return productService.updateProduct(productId, productRequestDTO);
    }

    @RequestMapping(value = "/{productId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);

        return ResponseEntity.noContent().build();
    }


}
