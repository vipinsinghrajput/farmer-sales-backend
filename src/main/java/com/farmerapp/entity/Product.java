package com.farmerapp.entity;

import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name cannot exceed 100 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotBlank(message = "Unit of measurement is required")
    private String unit; // e.g., kg, lbs, dozen

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.1", message = "Price must be greater than zero")
    private Double price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Category is required")
    private ProductCategory category;

    @ElementCollection
    private List<String> images; // Store image URLs

    @ManyToOne
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    private boolean available; // true if in stock, false if out of stock
}
