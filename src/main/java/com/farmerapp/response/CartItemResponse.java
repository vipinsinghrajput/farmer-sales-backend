package com.farmerapp.response;

import java.util.List;

import jakarta.persistence.ElementCollection;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {
//    private Long id;
//    private Long productId;
//    private String productName;
//    private Double price;
//    private Integer quantity;
	    private Long productId;
	    private String productName;
	    private int quantity;
	    private Double price;
	    @ElementCollection
	    private List<String> productimageUrl;
	    
	  
}
