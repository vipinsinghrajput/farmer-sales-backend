package com.farmerapp.request;


import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
@Data
public class OrderRequest {
	 @NotNull(message = "Consumer ID cannot be null")
	    private Long consumerId;

	    @NotNull(message = "Farmer ID cannot be null")
	    private Long farmerId;

	    @NotNull(message = "Total amount cannot be null")
	    @Positive(message = "Total amount must be greater than zero")
	    private Double totalAmount;

	    @NotBlank(message = "Delivery method is required")
	    private String deliveryMethod; // "Delivery" or "Pickup"

	    private String deliveryAddress; // Required if deliveryMethod = "Delivery"

	    private String pickupLocation; // Required if deliveryMethod = "Pickup"

	    @NotEmpty(message = "Order items cannot be empty")
	    @Valid // Ensures validation is applied to each item in the list
	    private List<OrderItemRequest> items;

	    @AssertTrue(message = "Either deliveryAddress or pickupLocation must be provided based on deliveryMethod")
	    public boolean isValidDeliveryDetails() {
	        if ("Delivery".equalsIgnoreCase(deliveryMethod)) {
	            return deliveryAddress != null && !deliveryAddress.trim().isEmpty();
	        } else if ("Pickup".equalsIgnoreCase(deliveryMethod)) {
	            return pickupLocation != null && !pickupLocation.trim().isEmpty();
	        }
	        return false; // Invalid deliveryMethod case
	    }
}
