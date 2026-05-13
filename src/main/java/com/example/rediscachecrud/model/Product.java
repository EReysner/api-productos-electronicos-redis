package com.example.rediscachecrud.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

// modelo de producto con anotaciones de validación y mapeo a la base de datos
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100)
    private String name;

    @Column(length = 1000)
    @Size(max = 1000)
    private String description;

    @Column(nullable = false)
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin("0.01")
    private BigDecimal price;

    @Column(nullable = false)
    @NotNull(message = "El stock es obligatorio")
    @Min(0)
    private Integer stock;
}
