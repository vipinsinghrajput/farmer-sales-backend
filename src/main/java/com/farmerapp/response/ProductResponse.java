package com.farmerapp.response;

import java.util.List;

import com.farmerapp.entity.ProductCategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private ProductCategory category;
    private Double price;
    private Integer stock;
    private String unit;
    private List<String> images; 
    private Long farmerId;
    private String farmerName;
    private boolean isAvailable; // New field added
}

