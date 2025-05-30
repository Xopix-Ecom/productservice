package com.xopix.productservice.services;

import com.xopix.productservice.dto.ProductRequestDTO;
import com.xopix.productservice.dto.ProductResponseDTO;
import com.xopix.productservice.exception.ResourceNotFoundException;
import com.xopix.productservice.models.Product;
import com.xopix.productservice.repository.ProductRepository;
import com.xopix.productservice.utils.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    public final ProductMapper productMapper;


    public ProductServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public Page<ProductResponseDTO> getAllProducts(String nameFilter, Pageable pageable) {
        Specification<Product> spec = (root, query, cb) ->
                nameFilter != null ?
                        cb.like(cb.lower(root.get("name")), "%" + nameFilter.toLowerCase() + "%") : null;
        return productRepository.findAll(spec, pageable)
                .map(productMapper::toDTO);
    }

    @Override
    public ProductResponseDTO getProductByProductId(Long productId) {
        return productMapper.toDTO(productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found")));
    }

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        Product product = productMapper.toEntity(productRequestDTO);
        productRepository.save(product);
        return productMapper.toDTO(product);
    }

    @Override
    public ProductResponseDTO updateProduct(Long productId, ProductRequestDTO productRequestDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setName(productRequestDTO.getName());
        product.setDescription(productRequestDTO.getDescription());
        product.setPrice(productRequestDTO.getPrice());
        product.setQuantity(productRequestDTO.getQuantity());
        product.setImageUrl(productRequestDTO.getImageUrl());

        productRepository.save(product);
        return productMapper.toDTO(product);
    }

    @Override
    public void deleteProduct(Long productId) {
        if(!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product does not exist");
        }
        productRepository.deleteById(productId);
    }
}
