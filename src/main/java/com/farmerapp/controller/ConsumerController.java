package com.farmerapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.farmerapp.payload.ConsumerDto;
import com.farmerapp.payload.ResetPasswordRequest;
import com.farmerapp.request.consumerUpdateRequest;
import com.farmerapp.response.ApiResponse;
import com.farmerapp.service.ConsumerService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/consumer")
@Validated
public class ConsumerController {


		@Autowired
		private ConsumerService consumerService;
		
		@PostMapping("/register")
	    public ResponseEntity<ApiResponse> registerFarmer(@Valid @RequestBody ConsumerDto consumer) {
	    	 	return ResponseEntity.ok(consumerService.registerConsumer(consumer));
	    }

	    @PostMapping("/verify-otp")
	    public  ResponseEntity<ApiResponse> verifyfarmerOtp(
	    		@RequestParam @NotBlank(message = "OTP cannot be blank") 
		@Pattern(regexp = "\\d+", message = "OTP must be numeric") 
        @Size(min = 4, max = 6, message = "OTP must be 4 to 6 digits")  String otp) {
	        return ResponseEntity.ok(consumerService.verifyconsumerOtp(otp));
	    }
	    
	    @PostMapping("/login")
	    public ResponseEntity<ApiResponse> farmerlogin(
	    @RequestParam("email") 
        @NotBlank(message = "Email cannot be blank") 
        @Email(message = "Invalid email format") String email,  
        @RequestParam("password") 
        @NotBlank(message = "Password cannot be blank") 
        @Size(min = 6, message = "Password must be at least 6 characters")String password ) {
			return  ResponseEntity.ok(consumerService.loginConsumer(email,password));
	    }

	    @PostMapping("/forgot-password")
	    public ResponseEntity<ApiResponse> forgotPassword(@RequestParam 
	            @NotBlank(message = "Email cannot be blank") 
	            @Email(message = "Invalid email format")String email) {
	    	return  ResponseEntity.ok(consumerService.forgotPassword(email));
	        
	    }

	    @PostMapping("/reset-password")
	    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
//	        consumerService.resetPassword(request);
	        return  ResponseEntity.ok(consumerService.resetPassword(request));
	    }
	    
	   
	    @GetMapping("/getall")
	    public ResponseEntity<ApiResponse> getAllConsumers(
	    		   @RequestParam(required = false) Long id,
	    	        @RequestParam(required = false) String name,
	    	        @RequestParam(required = false) String status, // Accept as String
	    	        @RequestParam(defaultValue = "0") int page,
	    	        @RequestParam(defaultValue = "10") int size) {
	    	
	    	 Boolean statusBool = null;
	    	    if ("true".equalsIgnoreCase(status)) statusBool = true;
	    	    else if ("false".equalsIgnoreCase(status)) statusBool = false;
	    	    
	        return ResponseEntity.ok(consumerService.getAllConsumers(id, name, statusBool, page, size));
	    }

	    @GetMapping("/getbyid")
	    public ResponseEntity<ApiResponse> getConsumer(@NotNull(message = "Consumer ID cannot be null") @RequestParam @Positive(message = "ID must be positive")  Long consumerId) {
	        return ResponseEntity.ok(consumerService.getConsumerById(consumerId));
	    }
	    @GetMapping("/getconsumer")
	    public ResponseEntity<ApiResponse> getConsumer() {
	        return ResponseEntity.ok(consumerService.getConsumer());
	    }

	    @PutMapping("/update")
	    public ResponseEntity<ApiResponse> updateConsumer(@Valid @RequestBody consumerUpdateRequest updatedConsumer) {
	        return ResponseEntity.ok(consumerService.updateConsumer(updatedConsumer));
	    }

	    @DeleteMapping("/delete")
	    public ResponseEntity<ApiResponse> softDeleteConsumer(  @NotBlank(message = "Email cannot be blank") 
        @Email(message = "Invalid email format")@RequestParam String email) {
	        return ResponseEntity.ok(consumerService.softDeleteConsumer(email));
	    }
	    
	    @PutMapping("/activate")
	    public ResponseEntity<ApiResponse> activateConsumer( @NotBlank(message = "Email cannot be blank") 
        @Email(message = "Invalid email format")@RequestParam String email) {
	        return ResponseEntity.ok(consumerService.activateConsumer(email));
	    }
}
