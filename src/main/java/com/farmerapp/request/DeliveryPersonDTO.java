package com.farmerapp.request;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPersonDTO {

	
	    @Size(min = 2, max = 50, message = "Name must be 2 to 50 characters long")
	    private String name;

	   
	    @Pattern(
	        regexp = "^[A-Z]{2}[0-9]{2}\\s?[A-Z]{1,2}\\s?[0-9]{1,4}$",
	        message = "Vehicle number must follow the Indian format (e.g., MP09 AB 1234)"
	    )
	    private String vehicleNumber;

	    
	    @Pattern(
	        regexp = "^[A-Z]{2}[0-9]{13}$",
	        message = "License number must follow the standard format (e.g., MH1420201234567)"
	    )
	    private String licenseNumber;
	
	
}
