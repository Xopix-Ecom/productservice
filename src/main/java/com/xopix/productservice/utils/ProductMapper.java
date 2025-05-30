package com.xopix.productservice.utils;

import com.xopix.productservice.dto.ProductRequestDTO;
import com.xopix.productservice.dto.ProductResponseDTO;
import com.xopix.productservice.models.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface  ProductMapper {

    // Converts Entity -> DTO (response)
     ProductResponseDTO toDTO(Product product);

    // Converts DTO -> Entity (for create/update)
    Product toEntity(ProductRequestDTO productRequestDTO);
}
