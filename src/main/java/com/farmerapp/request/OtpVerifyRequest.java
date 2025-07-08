package com.farmerapp.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerifyRequest {

	    @NotBlank(message = "Email is required")
	    @Email(message = "Invalid email format")
	    private String email;
	    @Size(min = 4, max= 8, message = "Minimum four number required")
	    private String otp;
	   

}
