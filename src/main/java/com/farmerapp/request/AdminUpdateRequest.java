package com.farmerapp.request;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUpdateRequest {
	

	    @NotBlank(message = "Admin name is required")
	    @Size(max = 100, message = "Admin name cannot exceed 100 characters")
	    private String name;

//	    @NotBlank(message = "Admin name is required")
//	    @Email(message = "Invalid email format")
//	    @Column(unique = true, nullable = false)
//	    private String email;

}
