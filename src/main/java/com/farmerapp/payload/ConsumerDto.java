package com.farmerapp.payload;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerDto {

	    @NotBlank(message = "Name is required")
	    @Size(max = 100, message = "Name cannot exceed 100 characters")
	    private String name;

	    @NotBlank(message = "Mobile number is required")
	    @Pattern(regexp = "\\d{10}", message = "Mobile number must be 10 digits")
	    @Column(unique = true)
	    private String mobileNumber;

	    @Email(message = "Invalid email format")
	    @NotBlank(message = "email is required")
	    @Column(unique = true, nullable = false)
	    private String email;

	    @NotBlank(message = "Password is required")
	    @Size(min = 8, message = "Password must be at least 8 characters long")
	    @Pattern(
	    	    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
	    	    message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
	    	)
	    private String password;

	    @NotBlank(message = "Delivery address is required")
	    @Size(max = 255, message = "Delivery address cannot exceed 255 characters")
	    private String deliveryAddress;

	    @Pattern(regexp = "\\d{6}", message = "Pincode must be 6 digits")
	    private String pincode;
}
