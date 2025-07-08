package com.farmerapp.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.farmerapp.entity.Farmer;
import com.farmerapp.entity.Product;
import com.farmerapp.entity.ProductCategory;
import com.farmerapp.exception.ProductNotFoundException;
import com.farmerapp.exception.UnauthorizedAccessException;
import com.farmerapp.payload.ProductDto;
import com.farmerapp.payload.ProductUpdatedDto;
import com.farmerapp.repository.FarmerRepository;
import com.farmerapp.repository.ProductRepository;
import com.farmerapp.response.ApiResponse;
import com.farmerapp.response.ProductResponse;
import com.farmerapp.specification.ProductSpecification;
import com.farmerapp.util.AppUtils;
import com.farmerapp.util.JwtUtil;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FarmerRepository farmerRepository;
    @Autowired
    private AppUtils utils;
    @Autowired
    private JwtUtil util;
    
    @Autowired
    private ImageUploadService imageUploadService;


    public Page<Product> getFilteredProducts(Long farmerId, String name, Double minPrice, Double maxPrice,
            ProductCategory category, Boolean available,
            int page, int size) {
Pageable pageable = PageRequest.of(page, size);

Specification<Product> spec = Specification
.where(ProductSpecification.belongsToFarmer(farmerId))
.and(ProductSpecification.hasNameLike(name))
.and(ProductSpecification.hasPriceBetween(minPrice, maxPrice))
.and(ProductSpecification.hasCategory(category))
.and(ProductSpecification.isAvailable(available != null ? available : true)); // default to true

return productRepository.findAll(spec, pageable);
}

    
    
    
    // Helper method to convert Product to ProductResponse
    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .price(product.getPrice())
                .stock(product.getStock())
                .unit(product.getUnit())
                .images(product.getImages())
                .farmerId(product.getFarmer().getId())
                .farmerName(product.getFarmer().getName()) // Assuming Farmer has a name field
                .isAvailable(product.isAvailable()) // Map new field
                .build();
    }
    
    
    public ApiResponse createProduct(ProductDto productDto,MultipartFile[] files) {
        
    	String token =utils.getTokenFromHeader();
    	String email=util.getUsername(token);
    	Farmer farmer = farmerRepository.findByEmail(email)
                .orElseThrow(() -> new ProductNotFoundException("Farmer not found with id: " + email));
    	
          
          List<String> imageUrls = new ArrayList<>();
          for (MultipartFile file : files) {
              if (file != null && !file.isEmpty()) {
                  String imageUrl = imageUploadService.uploadImage(file);
                  imageUrls.add(imageUrl);
              }
          }
  	  Optional<Product> existingProduct = productRepository.findByNameAndPriceAndFarmerId(
  	            productDto.getName(), productDto.getPrice(), farmer.getId());


        if (existingProduct.isPresent()) {
            // Increase stock instead of creating a new entry
            Product product = existingProduct.get();
            product.setStock(product.getStock() + productDto.getStock());
            product.setImages(imageUrls);
            product.setAvailable(product.getStock() > 0);
            productRepository.save(product);

            ProductResponse response = mapToResponse(product);
            return ApiResponse.builder()
                    .message("Product already exists. Stock updated successfully.")
                    .response(response)
                    .build();
        } else {
        	
        	 
            Product product = Product.builder()
                    .name(productDto.getName())
                    .description(productDto.getDescription())
                    .unit(productDto.getUnit())
                    .price(productDto.getPrice())
                    .stock(productDto.getStock())
                    .category(productDto.getCategory())
                    .images(imageUrls)
                    .farmer(farmer)
                    .available(productDto.getStock() != null && productDto.getStock() > 0)
                    .build();
        

        productRepository.save(product);
        ProductResponse response = mapToResponse(product);
        return ApiResponse.builder()
                .message("Product added successfully")
                .response(response)
                .build();
        }
    }
    
    
    
    
//    // CREATE a product
//    public ApiResponse createProduct(ProductDto productDto) {
//       
//    	String token =utils.getTokenFromHeader();
//    	String email=util.getUsername(token);
//    	Farmer farmer = farmerRepository.findByEmail(email)
//                .orElseThrow(() -> new ProductNotFoundException("Farmer not found with id: " + email));
//
////    	  Optional<Product> existingProduct = productRepository
////                  .findByNameIgnoreCaseAndFarmerId(productDto.getName(), farmer.getId());
//    	  
//    	  Optional<Product> existingProduct = productRepository.findByNameAndPriceAndFarmerId(
//    	            productDto.getName(), productDto.getPrice(), farmer.getId());
//
//
//
//          if (existingProduct.isPresent()) {
//              // Increase stock instead of creating a new entry
//              Product product = existingProduct.get();
//              product.setStock(product.getStock() + productDto.getStock());
//              productRepository.save(product);
//
//              ProductResponse response = mapToResponse(product);
//              return ApiResponse.builder()
//                      .message("Product already exists. Stock updated successfully.")
//                      .response(response)
//                      .build();
//          } else {
//        Product product = new Product();
//        product.setName(productDto.getName());
//        product.setDescription(productDto.getDescription());
//        product.setCategory(productDto.getCategory());
//        product.setPrice(productDto.getPrice());
//        product.setStock(productDto.getStock());
//        product.setFarmer(farmer);
//
//        Product savedProduct = productRepository.save(product);
//        ProductResponse response = mapToResponse(savedProduct);
//
//        return ApiResponse.builder()
//                .message("Product created successfully")
//                .response(response)
//                .build();
//          }
//    }

    // READ all products
    public ApiResponse getAllProducts() {
        List<Product> products = productRepository.findAll();
    	
        List<ProductResponse> responseList = products.stream().map(this::mapToResponse).collect(Collectors.toList());

        return ApiResponse.builder()
                .message("Fetched all products successfully")
                .response(responseList)
                .build();
    }
  
    
    // Read All Farmer Products
    public ApiResponse getAllProductsWithFarmerId(String name,Double minPrice,Double maxPrice, ProductCategory category,Boolean available,int page,int size) {
    	 String token = utils.getTokenFromHeader();
         String email = util.getUsername(token);
        
         
         Farmer farmer = farmerRepository.findByEmail(email)
                 .orElseThrow(() -> new ProductNotFoundException("Farmer not found with email: " + email));

         // Get filtered product page
         Page<Product> productPage = getFilteredProducts(farmer.getId(), name, minPrice, maxPrice, category, available, page, size);
         
//    	List<Product> products = productRepository.findByFarmerId(farmer.getId());;
    	
         List<ProductResponse> responseList = productPage.stream().map(this::mapToResponse).collect(Collectors.toList());

         Map<String, Object> responseMap = new HashMap<>();
         responseMap.put("products", responseList);
         responseMap.put("currentPage", productPage.getNumber());
         responseMap.put("totalPages", productPage.getTotalPages());
         responseMap.put("totalElements", productPage.getTotalElements());
         
//        List<ProductResponse> responseList = products.stream().map(this::mapToResponse).collect(Collectors.toList());

        return ApiResponse.builder()
                .message("Fetched all products successfully")
                .response(responseMap)
                .build();
    }
    
    
    public ApiResponse getAllFarmerProductsWithFarmerId(Long farmerId ,String name,Double minPrice,Double maxPrice, ProductCategory category,Boolean available,int page,int size) {
   	
        
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new ProductNotFoundException("Farmer not found with id: " + farmerId));

   	
//   	List<Product> products = productRepository.findByFarmerId(farmer.getId());;
   	
    // Get filtered product page
    Page<Product> productPage = getFilteredProducts(farmer.getId(), name, minPrice, maxPrice, category, available, page, size);

   	
       List<ProductResponse> responseList = productPage.stream().map(this::mapToResponse).collect(Collectors.toList());

       Map<String, Object> responseMap = new HashMap<>();
       responseMap.put("products", responseList);
       responseMap.put("currentPage", productPage.getNumber());
       responseMap.put("totalPages", productPage.getTotalPages());
       responseMap.put("totalElements", productPage.getTotalElements());
       
       return ApiResponse.builder()
               .message("Fetched all products successfully")
               .response(responseMap)
               .build();
   }
    
    
    // READ product by ID
    public ApiResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        ProductResponse response = mapToResponse(product);

        return ApiResponse.builder()
                .message("Product fetched successfully")
                .response(response)
                .build();
    }

    // READ product by FarmerId
    public ApiResponse getProductByFarmerId(Long id) {
       
    	  String token = utils.getTokenFromHeader();
          String email = util.getUsername(token);
          
          Farmer farmer = farmerRepository.findByEmail(email)
                  .orElseThrow(() -> new ProductNotFoundException("Farmer not found with email: " + email));

    	
    	Product product = productRepository.findByIdAndFarmerId(id,farmer.getId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        ProductResponse response = mapToResponse(product);

        return ApiResponse.builder()
                .message("Product fetched successfully")
                .response(response)
                .build();
    }
    
    
    public ApiResponse updateProduct(Long id, ProductUpdatedDto productDto, MultipartFile[] files ) throws UnauthorizedAccessException {
        String token = utils.getTokenFromHeader();
        String email = util.getUsername(token);
        
        Farmer farmer = farmerRepository.findByEmail(email)
                .orElseThrow(() -> new ProductNotFoundException("Farmer not found with email: " + email));

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        if (!product.getFarmer().getId().equals(farmer.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to update this product.");
        }

        
        // If new images are uploaded, upload and replace existing ones
        List<String> uploadedImageUrls = new ArrayList<>();
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    String imageUrl = imageUploadService.uploadImage(file);
                    uploadedImageUrls.add(imageUrl);
                }
            }
            productDto.setImages(uploadedImageUrls);
        } else {
            // Keep existing images if no new ones provided
            productDto.setImages(product.getImages());
        }
    
        product = Product.builder()
                .id(product.getId()) 
                .name(productDto.getName())
                .description(productDto.getDescription())
                .category(productDto.getCategory())
                .unit(productDto.getUnit())
                .price(productDto.getPrice())
                .stock(productDto.getStock())
                .images(productDto.getImages())
                .farmer(farmer) 
                .available(productDto.getStock() != null && productDto.getStock() > 0) 
                .build();

        Product updatedProduct = productRepository.save(product);

        ProductResponse response = mapToResponse(updatedProduct);

        return ApiResponse.builder()
                .message("Product updated successfully")
                .response(response)
                .build();
    }


    
    
//    // UPDATE a product
//    public ApiResponse updateProduct(Long id, ProductDto productDto) {
//       
//    	
//    	Product product = productRepository.findById(id)
//                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
//
//    	
//        product.setName(productDto.getName());
//        product.setDescription(productDto.getDescription());
//        product.setCategory(productDto.getCategory());
//        product.setPrice(productDto.getPrice());
//        product.setStock(productDto.getStock());
//
//        Product updatedProduct = productRepository.save(product);
//        ProductResponse response = mapToResponse(updatedProduct);
//
//        return ApiResponse.builder()
//                .message("Product updated successfully")
//                .response(response)
//                .build();
//    }

    // DELETE a product
    public ApiResponse deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

//        productRepository.delete(product);
        product.setAvailable(false);
        product.setStock(0);
        productRepository.save(product);

        return ApiResponse.builder()
                .message("Product deleted successfully")
                .response(null)
                .build();
    }
}
