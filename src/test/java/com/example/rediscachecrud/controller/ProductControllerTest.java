package com.example.rediscachecrud.controller;

import com.example.rediscachecrud.dto.ProductCreateDTO;
import com.example.rediscachecrud.dto.ProductDTO;
import com.example.rediscachecrud.dto.ProductUpdateDTO;
import com.example.rediscachecrud.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("Tests para ProductController")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private ProductDTO productDTO;
    private ProductCreateDTO productCreateDTO;
    private ProductUpdateDTO productUpdateDTO;

    @BeforeEach
    void setUp() {
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
    @DisplayName("GET /api/products - Debe retornar todos los productos")
    void testGetAllProducts() throws Exception {
        List<ProductDTO> products = Arrays.asList(productDTO);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Laptop Dell"));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("GET /api/products/paginated - Debe retornar productos paginados")
    void testGetProductsPaginated() throws Exception {
        Page<ProductDTO> page = new PageImpl<>(Arrays.asList(productDTO), PageRequest.of(0, 10), 1);
        when(productService.getProductsPaginated(any())).thenReturn(page);

        mockMvc.perform(get("/api/products/paginated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.totalElements").value(1));

        verify(productService, times(1)).getProductsPaginated(any());
    }

    @Test
    @DisplayName("GET /api/products/{id} - Debe retornar un producto por ID")
    void testGetProductById() throws Exception {
        when(productService.getProductById(1L)).thenReturn(productDTO);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop Dell"));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    @DisplayName("POST /api/products - Debe crear un producto")
    void testCreateProduct() throws Exception {
        when(productService.createProduct(any(ProductCreateDTO.class))).thenReturn(productDTO);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop Dell"));

        verify(productService, times(1)).createProduct(any(ProductCreateDTO.class));
    }

    @Test
    @DisplayName("POST /api/products - Debe fallar con datos inválidos")
    void testCreateProductInvalidData() throws Exception {
        ProductCreateDTO invalidDTO = new ProductCreateDTO();
        invalidDTO.setName("AB"); // Menos de 3 caracteres
        invalidDTO.setPrice(new BigDecimal("-10")); // Precio negativo

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any());
    }

    @Test
    @DisplayName("PUT /api/products/{id} - Debe actualizar un producto")
    void testUpdateProduct() throws Exception {
        when(productService.updateProduct(eq(1L), any(ProductUpdateDTO.class))).thenReturn(productDTO);

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(productService, times(1)).updateProduct(eq(1L), any(ProductUpdateDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - Debe eliminar un producto")
    void testDeleteProduct() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }
}
