package com.xopix.productservice.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateRequestDTO {

    private String name;
    private String description;
    @Min(value = 0, message = "Price cannot be negative")
    private BigDecimal price;
    private String category;
    private String imageUrl;
    // stockQuantity update is typically handled by Inventory Service in a mature system
    // but can be allowed here for initial setup.
    // @Min(value = 0, message = "Stock quantity cannot be negative")
    // private Integer stockQuantity;
}
