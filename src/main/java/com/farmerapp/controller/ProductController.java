package com.farmerapp.controller;

import com.farmerapp.entity.ProductCategory;
import com.farmerapp.exception.UnauthorizedAccessException;
import com.farmerapp.payload.ProductDto;
import com.farmerapp.payload.ProductUpdatedDto;
import com.farmerapp.response.ApiResponse;
import com.farmerapp.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/products")
@Validated 
public class ProductController {

    @Autowired
    private ProductService productService;

    // CREATE Product
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createProduct(@Valid @ModelAttribute ProductDto productDto , @RequestParam() MultipartFile[] files) {
        return ResponseEntity.ok(productService.createProduct(productDto,files));
    }

    @GetMapping("/getall")
    public ResponseEntity<ApiResponse> getAllProducts() {
    	System.out.println("hellop"+productService.getAllProducts());
        return ResponseEntity.ok(productService.getAllProducts());
    }
    
    @GetMapping("/getallfarmer")
    public ResponseEntity<ApiResponse> getAllProductsWithFarmerId(
    		 @RequestParam(required = false) String name,
             @RequestParam(required = false) Double minPrice,
             @RequestParam(required = false) Double maxPrice,
             @RequestParam(required = false) ProductCategory category,
             @RequestParam(required = false) Boolean available,
             @RequestParam(defaultValue = "0") int page,
             @RequestParam(defaultValue = "10") int size
    		) {
    	
        return ResponseEntity.ok(productService.getAllProductsWithFarmerId(name,minPrice,maxPrice, category,available,page,size));
    }
    
    
    @GetMapping("/getallfarmerproducts")
    public ResponseEntity<ApiResponse> getAllFarmerProductsWithFarmerId(
    		@NotNull(message = " Farmer ID cannot be null") @RequestParam @Positive(message = "ID must be positive") Long farmerId	,
    		 @RequestParam(required = false) String name,
             @RequestParam(required = false) Double minPrice,
             @RequestParam(required = false) Double maxPrice,
             @RequestParam(required = false) ProductCategory category,
             @RequestParam(required = false) Boolean available,
             @RequestParam(defaultValue = "0") int page,
             @RequestParam(defaultValue = "10") int size
    		
    		) {
        return ResponseEntity.ok(productService.getAllFarmerProductsWithFarmerId(farmerId,name,minPrice,maxPrice, category,available,page,size));
    }
    
    @GetMapping("/getbyfarmerid")
    public ResponseEntity<ApiResponse> getProductByFarmerId(  @NotNull(message = "Product ID cannot be null") @RequestParam @Positive(message = "ID must be positive") Long id) {
        return ResponseEntity.ok(productService.getProductByFarmerId(id));
    }

    @GetMapping("/getbyid")
    public ResponseEntity<ApiResponse> getProductById(  @NotNull(message = "Product ID cannot be null") @RequestParam @Positive(message = "ID must be positive") Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    
    // UPDATE Product by ID 
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateProduct(
            @RequestParam @Positive(message = "ID must be positive") Long id,
            @Valid @ModelAttribute ProductUpdatedDto productUpdatedDto,
            @RequestParam() MultipartFile[] files) throws UnauthorizedAccessException {
        return ResponseEntity.ok(productService.updateProduct(id, productUpdatedDto,files));
    }

    // DELETE Product by ID 
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deleteProduct(@RequestParam @Positive(message = "ID must be positive") Long id) {
        return ResponseEntity.ok(productService.deleteProduct(id));
    }
}
