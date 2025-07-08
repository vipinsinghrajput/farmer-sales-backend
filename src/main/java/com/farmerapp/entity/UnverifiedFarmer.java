package com.farmerapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
 public class UnverifiedFarmer {

	
	   
//	    public UnverifiedFarmer(String email2, String mobileNumber2, String encode, String additionalData2) {
//		// TODO Auto-generated constructor stub
//	    	
//	    	this.email=email2;
//	    	this.mobileNumber=mobileNumber2;
//	    	this.pincode=encode;
//	    	this.additionalData=additionalData2;
//	}

		
	    @NotBlank(message = "Name is required")
	    @Size(max = 100, message = "Name cannot exceed 100 characters")
	    private String name;

	    @NotBlank(message = "Mobile number is required")
	    @Pattern(regexp = "\\d{10}", message = "Mobile number must be 10 digits")
//	    @Column(unique = true)
	    private String mobileNumber;
        
	    @Id
//	    @Email(message = "Invalid email format")
	    @Column( nullable = false)
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
//	    private String additionalData;

//	    @Pattern(regexp = "[A-Za-z0-9]{5,50}", message = "Invalid farm license number")
	    private String farmLicenseNumber;
}
