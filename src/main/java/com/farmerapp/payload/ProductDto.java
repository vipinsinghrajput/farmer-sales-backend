package com.farmerapp.payload;

import com.farmerapp.entity.ProductCategory;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;


@Data
public class ProductDto {

    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name cannot exceed 100 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotBlank(message = "Unit is required")
    private String unit; // e.g., kg, lbs

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.1", message = "Price must be greater than zero")
    private Double price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    @NotNull(message = "Category is required")
    private ProductCategory category;

   
//    private List<String> images;


	
}
