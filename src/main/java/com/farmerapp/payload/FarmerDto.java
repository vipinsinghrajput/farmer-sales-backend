package com.farmerapp.payload;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmerDto {

	    @NotBlank(message = "Name is required")
	    @Size(max = 100, message = "Name cannot exceed 100 characters")
	    private String name;

	    @NotBlank(message = "Mobile number is required")
	    @Pattern(regexp = "\\d{10}", message = "Mobile number must be 10 digits")
	    @Column(unique = true)
	    private String mobileNumber;

	    @Email(message = "Invalid email format")
	    @Column(unique = true, nullable = false)
	    @NotBlank(message = "email is required")
	    private String email;

	    @NotBlank(message = "Password is required")
	    @Size(min = 8, message = "Password must be at least 8 characters long")
	    private String password;

	    @NotBlank(message = "Farm name is required")
	    @Size(max = 100, message = "Farm name cannot exceed 100 characters")
	    private String farmName;

	    @NotBlank(message = "Farm address is required")
	    @Size(max = 255, message = "Farm address cannot exceed 255 characters")
	    private String farmAddress;
	    
	    @NotBlank(message = "Farm city is required")
	    @Size(max = 20, message = "Farm city cannot exceed 20 characters")
	    private String farmCity;
	    
	    @NotBlank(message = "Farm State is required")
	    @Size(max = 20, message = "Farm address cannot exceed 20 characters")
	    private String farmState;
	    
	    @NotBlank(message = "Farm Country is required")
	    @Size(max = 20, message = "Farm Country cannot exceed 20 characters")
	    private String farmCountry;

	    @Pattern(regexp = "\\d{6}", message = "Pincode must be 6 digits")
	    private String pincode;

	    private String farmDescription;

	    @Pattern(regexp = "[A-Za-z0-9]{5,50}", message = "Invalid farm license number")
	    private String farmLicenseNumber;
}
