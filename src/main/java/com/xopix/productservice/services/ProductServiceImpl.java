package com.xopix.productservice.services;

import com.xopix.productservice.dto.ProductRequestDTO;
import com.xopix.productservice.exception.ProductAlreadyExistsException;
import com.xopix.productservice.exception.ProductNotFoundException;
import com.xopix.productservice.models.Product;
import com.xopix.productservice.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@Slf4j
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Page<Product> getAllProducts(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        log.info("Fetching all products (page: {}, size: {}, sort: {})", page, size, sortBy);
        return productRepository.findAll(pageable);
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param productId The ID of the product.
     * @return The Product entity.
     * @throws ProductNotFoundException if the product is not found.
     */
    @Override
    public Product getProductByProductId(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }


    /**
     * Creates a new product.
     *
     * @param request The product creation request.
     * @return The created Product entity.
     * @throws ProductAlreadyExistsException if a product with the given SKU already exists.
     */
    @Override
    public Product createProduct(ProductRequestDTO request) throws ProductAlreadyExistsException {
        if (productRepository.existsBySku(request.getSku())) {
            log.warn("Attempted to create product with existing SKU: {}", request.getSku());
            throw new ProductAlreadyExistsException("Product with SKU " + request.getSku() + " already exists.");
        }
        Product newProduct = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .sku(request.getSku())
                .category(request.getCategory())
                .imageUrl(request.getImageUrl())
                .stockQuantity(request.getStockQuantity())
                .createdAt(LocalDateTime.now()) // JPA Auditing will handle this too if enabled
                .build();

        log.info("Creating new product with SKU: {}", request.getSku());

        productRepository.save(newProduct);
        log.info("Creating new product with SKU: {}", request.getSku());
        return productRepository.save(newProduct);
    }


    /**
     * Updates an existing product's details.
     *
     * @param productId     The ID of the product to update.
     * @param productRequestDTO The product update request.
     * @return The updated Product entity.
     * @throws ProductNotFoundException if the product is not found.
     */
    @Override
    public Product updateProduct(String  productId, ProductRequestDTO productRequestDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " not found."));

        Optional.ofNullable(productRequestDTO.getName()).ifPresent(product::setName);
        Optional.ofNullable(productRequestDTO.getDescription()).ifPresent(product::setDescription);
        Optional.ofNullable(productRequestDTO.getPrice()).ifPresent(product::setPrice);
        Optional.ofNullable(productRequestDTO.getCategory()).ifPresent(product::setCategory);
        Optional.ofNullable(productRequestDTO.getImageUrl()).ifPresent(product::setImageUrl);
        // Do not update stockQuantity here as it will be managed by Inventory Service in later phases.
        // If needed for initial setup, uncomment the relevant line in ProductUpdateRequest.java and add here:
        // Optional.ofNullable(request.getStockQuantity()).ifPresent(existingProduct::setStockQuantity);
        product.setUpdatedAt(LocalDateTime.now());
        log.info("Updating product with ID: {}", productId);
        return productRepository.save(product);
    }


    /**
     * Deletes a product by its ID.
     *
     * @param productId The ID of the product to delete.
     * @throws ProductNotFoundException if the product is not found.
     */
    @Override
    public void deleteProduct(String  productId) {
        if(!productRepository.existsById(productId)) {
            throw new ProductNotFoundException("Product does not exist");
        }
        log.info("Deleted product with ID: {}", productId);
        productRepository.deleteById(productId);
    }
}
