package com.farmerapp.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderItemSummary {

	    
	        private String productName;
	        private int quantity;
	        private double pricePerUnit;
	        private double subtotal;

}
