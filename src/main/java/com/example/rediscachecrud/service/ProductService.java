package com.example.rediscachecrud.service;

import com.example.rediscachecrud.dto.ProductCreateDTO;
import com.example.rediscachecrud.dto.ProductDTO;
import com.example.rediscachecrud.dto.ProductUpdateDTO;
import com.example.rediscachecrud.exception.ResourceNotFoundException;
import com.example.rediscachecrud.mapper.ProductMapper;
import com.example.rediscachecrud.model.Product;
import com.example.rediscachecrud.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Cacheable(value = "products", key = "'allProducts'")
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        log.info("Consultando todos los productos");
        return productRepository.findAll().stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> getProductsPaginated(Pageable pageable) {
        log.info("Consultando productos paginados - página: {}, tamaño: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return productRepository.findAll(pageable)
                .map(productMapper::toDTO);
    }

    // @Cacheable se utiliza para: Obtener los detalles de un producto específico por su ID, con almacenamiento en caché para mejorar el rendimiento.
    @Cacheable(value = "products", key = "#id")
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        log.info("Consultando producto con id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
        return productMapper.toDTO(product);
    }

    // @CacheEvict se utiliza para: Eliminar el caché de todos los productos cuando se crea un nuevo producto.
    // con el objetivo de mantener la coherencia de los datos en la caché después de una operación de escritura (creación, actualización o eliminación).
    @CacheEvict(value = "products", key = "'allProducts'")
    @Transactional
    public ProductDTO createProduct(ProductCreateDTO productCreateDTO) {
        log.info("Creando producto: {}", productCreateDTO.getName());
        Product product = productMapper.toEntity(productCreateDTO);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDTO(savedProduct);
    }

    // @CachePut se utiliza para: Actualizar el caché de un producto específico después de modificar sus detalles.
    @CachePut(value = "products", key = "#id")
    @CacheEvict(value = "products", key = "'allProducts'")
    @Transactional
    public ProductDTO updateProduct(Long id, ProductUpdateDTO productUpdateDTO) {
        log.info("Actualizando producto id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));

        productMapper.updateEntityFromDTO(productUpdateDTO, product);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toDTO(updatedProduct);
    }

    @CacheEvict(value = "products", allEntries = true)
    @Transactional
    public void deleteProduct(Long id) {
        log.info("Eliminando producto id: {}", id);
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto", "id", id);
        }
        productRepository.deleteById(id);
    }
}
