package com.farmerapp.request;

import jakarta.persistence.Column;
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
public class farmerUpdateRequest {

    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

//    @Pattern(regexp = "\\d{10}", message = "Mobile number must be 10 digits")
//    @Column(unique = true)
//    private String mobileNumber;

//    @Email(message = "Invalid email format")
//    @Column(unique = true, nullable = false)
//    @NotBlank(message = "email is required")
//    private String email;

    @Size(max = 100, message = "Farm name cannot exceed 100 characters")
    private String farmName;

    @Size(max = 255, message = "Farm address cannot exceed 255 characters")
    private String farmAddress;
    
    @Size(max = 20, message = "Farm city cannot exceed 20 characters")
    private String farmCity;
    
    @Size(max = 20, message = "Farm address cannot exceed 20 characters")
    private String farmState;
    
    @Size(max = 20, message = "Farm Country cannot exceed 20 characters")
    private String farmCountry;

    @Pattern(regexp = "\\d{6}", message = "Pincode must be 6 digits")
    private String pincode;

    private String farmDescription;

    @Pattern(regexp = "[A-Za-z0-9]{5,50}", message = "Invalid farm license number")
    private String farmLicenseNumber;
}
