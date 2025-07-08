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
public class consumerUpdateRequest {


	         @NotBlank(message = "Name is required")
		    @Size(max = 100, message = "Name cannot exceed 100 characters")
		    private String name;

		    @Pattern(regexp = "\\d{10}", message = "Mobile number must be 10 digits")
		    @Column(unique = true)
		    private String mobileNumber;

		    @Email(message = "Invalid email format")
		    @NotBlank(message = "email is required")
		    @Column(unique = true, nullable = false)
		    private String email;

		    @NotBlank(message = "address is required")
		    @Size(max = 255, message = "Delivery address cannot exceed 255 characters")
		    private String deliveryAddress;

		    @NotBlank(message = "Pincode is required")
		    @Pattern(regexp = "\\d{6}", message = "Pincode must be 6 digits")
		    private String pincode;

}
