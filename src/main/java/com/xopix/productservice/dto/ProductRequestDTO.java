package com.xopix.productservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
public class ProductRequestDTO {

    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price cannot be negative")
    private BigDecimal price;

    @NotBlank(message = "SKU is required")
    private String sku; // Stock Keeping Unit, unique identifier

    @NotBlank(message = "Category is required")
    private String category;

    private String imageUrl;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    private int stockQuantity;
}
