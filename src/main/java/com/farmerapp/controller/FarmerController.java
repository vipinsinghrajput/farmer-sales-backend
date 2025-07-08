package com.farmerapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.farmerapp.payload.FarmerDto;
import com.farmerapp.payload.ResetPasswordRequest;
import com.farmerapp.request.consumerUpdateRequest;
import com.farmerapp.request.farmerUpdateRequest;
import com.farmerapp.response.ApiResponse;
import com.farmerapp.service.FarmerService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/farmer")
@Validated
public class FarmerController {

	@Autowired
	private FarmerService farmerService;
	
	@PostMapping("/register")
    public ResponseEntity<ApiResponse> registerFarmer(@Valid @RequestBody FarmerDto farmer) {
		System.err.println(farmer);
    	 	return ResponseEntity.ok(farmerService.registerFarmer(farmer));
    }

    @PostMapping("/verify-otp")
    public  ResponseEntity<ApiResponse> verifyfarmerOtp(  
    		@RequestParam @NotBlank(message = "OTP cannot be blank") 
    		@Pattern(regexp = "\\d+", message = "OTP must be numeric") 
            @Size(min = 4, max = 6, message = "OTP must be 4 to 6 digits")  String otp) {
        return ResponseEntity.ok(farmerService.verifyfarmerOtp(otp));
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> farmerlogin(
    		@RequestParam("email") 
            @NotBlank(message = "Email cannot be blank") 
            @Email(message = "Invalid email format") String email,  
            @RequestParam("password") 
            @NotBlank(message = "Password cannot be blank") 
            @Size(min = 6, message = "Password must be at least 6 characters")String password ) {
		return  ResponseEntity.ok(farmerService.loginFarmer(email,password));
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(
    		@RequestParam 
            @NotBlank(message = "Email cannot be blank") 
            @Email(message = "Invalid email format")String email) {
    	return  ResponseEntity.ok(farmerService.forgotPassword(email));
        
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
//        farmerService.resetPassword(request);
        return  ResponseEntity.ok(farmerService.resetPassword(request));
    }
    
    @GetMapping("/getall")
    public ResponseEntity<ApiResponse> getAllFarmers(  
    		@RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
    	
    	  Boolean statusBool = null;
    	    if ("true".equalsIgnoreCase(status)) {
    	        statusBool = true;
    	    } else if ("false".equalsIgnoreCase(status)) {
    	        statusBool = false;
    	    }
    	
        return ResponseEntity.ok(farmerService.getAllFarmers(id, name, statusBool, page, size));
    }

    @GetMapping("/getbyid")
    public ResponseEntity<ApiResponse> getFarmer(@NotNull(message = "Farmer ID cannot be null") @RequestParam @Positive(message = "ID must be positive")  Long farmerId) {
        return ResponseEntity.ok(farmerService.getFarmerById(farmerId));
    }

    @GetMapping("/getfarmer")
    public ResponseEntity<ApiResponse> getFarmer() {
        return ResponseEntity.ok(farmerService.getFarmer());
    }
    
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateFarmer(@Valid @RequestBody farmerUpdateRequest updatedFarmer) {
        return ResponseEntity.ok(farmerService.updateFarmer(updatedFarmer));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> softDeleteFarmer(  @NotBlank(message = "Email cannot be blank") 
    @Email(message = "Invalid email format")@RequestParam String email) {
        return ResponseEntity.ok(farmerService.softDeleteFarmer(email));
    }
    
    @PutMapping("/activate")
    public ResponseEntity<ApiResponse> activateFarmer( @NotBlank(message = "Email cannot be blank") 
    @Email(message = "Invalid email format")@RequestParam String email) {
        return ResponseEntity.ok(farmerService.activateFarmer(email));
    }
    
}
