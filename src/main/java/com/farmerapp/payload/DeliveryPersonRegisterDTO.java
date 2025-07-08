package com.farmerapp.payload;

import org.springframework.validation.annotation.Validated;

import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryPersonRegisterDTO {

	   
	    @NotBlank(message = "Name is required")
	    @Size(min = 2, max = 50, message = "Name must be 2 to 50 characters long")
	    private String name;

	    @NotBlank(message = "Mobile number is required")
	    @Pattern(
	        regexp = "^[6-9]\\d{9}$",
	        message = "Mobile number must be a valid 10-digit Indian number starting with 6-9"
	    )
	    private String mobile;

	    @NotBlank(message = "Email is required")
	    @Email(message = "Invalid email format")
	    private String email;

	    @NotBlank(message = "Password is required")
	    @Size(min = 8, message = "Password must be at least 8 characters long")
	    private String password;

	    @NotBlank(message = "Vehicle number is required")
	    @Pattern(
	        regexp = "^[A-Z]{2}[0-9]{2}\\s?[A-Z]{1,2}\\s?[0-9]{1,4}$",
	        message = "Vehicle number must follow the Indian format (e.g., MP09 AB 1234)"
	    )
	    private String vehicleNumber;

	    @NotBlank(message = "License number is required")
	    @Pattern(
	        regexp = "^[A-Z]{2}[0-9]{13}$",
	        message = "License number must follow the standard format (e.g., MH1420201234567)"
	    )
	    private String licenseNumber;
}
