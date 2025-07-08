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
public class UnverifiedConsumer {

	    @NotBlank(message = "Name is required")
	    @Size(max = 100, message = "Name cannot exceed 100 characters")
	    private String name;

	    @NotBlank(message = "Mobile number is required")
	    @Pattern(regexp = "\\d{10}", message = "Mobile number must be 10 digits")
	    @Column(unique = true)
	    private String mobileNumber;

	    @Id
	    @Email(message = "Invalid email format")
	    @Column(unique = true, nullable = false)
	    private String email;

	    @NotBlank(message = "Password is required")
	    @Size(min = 8, message = "Password must be at least 8 characters long")
	    private String password;

	    @NotBlank(message = "Delivery address is required")
	    @Size(max = 255, message = "Delivery address cannot exceed 255 characters")
	    private String deliveryAddress;

	    @Pattern(regexp = "\\d{6}", message = "Pincode must be 6 digits")
	    private String pincode;
}
