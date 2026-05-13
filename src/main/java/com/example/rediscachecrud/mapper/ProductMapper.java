package com.example.rediscachecrud.mapper;

import com.example.rediscachecrud.dto.ProductCreateDTO;
import com.example.rediscachecrud.dto.ProductDTO;
import com.example.rediscachecrud.dto.ProductUpdateDTO;
import com.example.rediscachecrud.model.Product;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

//mapper para convertir entre entidades y DTOs, utilizando ModelMapper para 
//simplificar el proceso de mapeo entre objetos.
@Component
public class ProductMapper {

    private final ModelMapper modelMapper;

    public ProductMapper() {
        this.modelMapper = new ModelMapper();
    }

    public ProductDTO toDTO(Product product) {
        return modelMapper.map(product, ProductDTO.class);
    }

    public Product toEntity(ProductCreateDTO dto) {
        return modelMapper.map(dto, Product.class);
    }

    public Product toEntity(ProductUpdateDTO dto) {
        return modelMapper.map(dto, Product.class);
    }

    public void updateEntityFromDTO(ProductUpdateDTO dto, Product product) {
        modelMapper.map(dto, product);
    }
}
