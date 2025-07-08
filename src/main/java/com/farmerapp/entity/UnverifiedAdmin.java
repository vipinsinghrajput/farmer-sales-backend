package com.farmerapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class UnverifiedAdmin {

	    @NotBlank(message = "Admin name is required")
	    @Size(max = 100, message = "Admin name cannot exceed 100 characters")
	    private String name;

	    @Id
	    @Email(message = "Invalid email format")
	    @Column(unique = true, nullable = false)
	    private String email;

	    @NotBlank(message = "Password is required")
	    private String password;
}
