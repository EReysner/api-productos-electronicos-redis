package com.example.rediscachecrud.service;

import com.example.rediscachecrud.dto.ProductCreateDTO;
import com.example.rediscachecrud.dto.ProductDTO;
import com.example.rediscachecrud.dto.ProductUpdateDTO;
import com.example.rediscachecrud.exception.ResourceNotFoundException;
import com.example.rediscachecrud.mapper.ProductMapper;
import com.example.rediscachecrud.model.Product;
import com.example.rediscachecrud.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para ProductService")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDTO productDTO;
    private ProductCreateDTO productCreateDTO;
    private ProductUpdateDTO productUpdateDTO;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Laptop Dell");
        product.setDescription("Laptop de alta gama");
        product.setPrice(new BigDecimal("1299.99"));
        product.setStock(10);

        productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Laptop Dell");
        productDTO.setDescription("Laptop de alta gama");
        productDTO.setPrice(new BigDecimal("1299.99"));
        productDTO.setStock(10);

        productCreateDTO = new ProductCreateDTO();
        productCreateDTO.setName("Laptop Dell");
        productCreateDTO.setDescription("Laptop de alta gama");
        productCreateDTO.setPrice(new BigDecimal("1299.99"));
        productCreateDTO.setStock(10);

        productUpdateDTO = new ProductUpdateDTO();
        productUpdateDTO.setName("Laptop Dell Actualizada");
        productUpdateDTO.setDescription("Laptop premium");
        productUpdateDTO.setPrice(new BigDecimal("1499.99"));
        productUpdateDTO.setStock(5);
    }

    @Test
    @DisplayName("Debe obtener todos los productos")
    void testGetAllProducts() {
        // Arrange
        List<Product> products = Arrays.asList(product);
        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.toDTO(any(Product.class))).thenReturn(productDTO);

        // Act
        List<ProductDTO> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener productos paginados")
    void testGetProductsPaginated() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(Arrays.asList(product));
        when(productRepository.findAll(pageable)).thenReturn(productPage);
        when(productMapper.toDTO(any(Product.class))).thenReturn(productDTO);

        // Act
        Page<ProductDTO> result = productService.getProductsPaginated(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Debe obtener un producto por ID")
    void testGetProductById() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        // Act
        ProductDTO result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Laptop Dell", result.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el producto no existe")
    void testGetProductByIdNotFound() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductById(999L);
        });
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe crear un producto")
    void testCreateProduct() {
        // Arrange
        when(productMapper.toEntity(productCreateDTO)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        // Act
        ProductDTO result = productService.createProduct(productCreateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Laptop Dell", result.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Debe actualizar un producto")
    void testUpdateProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        // Act
        ProductDTO result = productService.updateProduct(1L, productUpdateDTO);

        // Assert
        assertNotNull(result);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productMapper, times(1)).updateEntityFromDTO(productUpdateDTO, product);
    }

    @Test
    @DisplayName("Debe eliminar un producto")
    void testDeleteProduct() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar producto inexistente")
    void testDeleteProductNotFound() {
        // Arrange
        when(productRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.deleteProduct(999L);
        });
        verify(productRepository, times(1)).existsById(999L);
        verify(productRepository, never()).deleteById(anyLong());
    }
}
