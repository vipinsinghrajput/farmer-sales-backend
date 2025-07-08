package com.farmerapp.controller;

import java.util.List;

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

import com.farmerapp.entity.DeliveryPerson;
import com.farmerapp.entity.DeliveryPerson.Status;
import com.farmerapp.payload.ConsumerDto;
import com.farmerapp.payload.DeliveryLoginDTO;
import com.farmerapp.payload.DeliveryPersonRegisterDTO;
import com.farmerapp.payload.FarmerDto;
import com.farmerapp.payload.ResetPasswordRequest;
import com.farmerapp.request.DeliveryPersonDTO;
import com.farmerapp.request.OtpVerifyRequest;
import com.farmerapp.request.farmerUpdateRequest;
import com.farmerapp.response.ApiResponse;
import com.farmerapp.response.DeliveryPersonResponse;
import com.farmerapp.service.DeliveryPersonService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/delivery")
@Validated
public class DeliveryPersonController {

	 @Autowired
	    private DeliveryPersonService service;


	 @PostMapping("/register")
	    public ResponseEntity<ApiResponse> register(@Valid @RequestBody DeliveryPersonRegisterDTO dto) {
	    	 	return ResponseEntity.ok(service.register(dto));
	    }

	    @PostMapping("/verify-otp")
	    public  ResponseEntity<ApiResponse> verifyOtp(  
	    		@RequestParam @NotBlank(message = "OTP cannot be blank") 
	    		@Pattern(regexp = "\\d+", message = "OTP must be numeric") 
	            @Size(min = 4, max = 6, message = "OTP must be 4 to 6 digits")  String otp) {
	        return ResponseEntity.ok(service.verifyOtp(otp));
	    }
	    
	    @PostMapping("/login")
	    public ResponseEntity<ApiResponse> login(
	    		@RequestParam("email") 
	            @NotBlank(message = "Email cannot be blank") 
	            @Email(message = "Invalid email format") String email,  
	            @RequestParam("password") 
	            @NotBlank(message = "Password cannot be blank") 
	            @Size(min = 6, message = "Password must be at least 6 characters")String password ) {
			return  ResponseEntity.ok(service.login(email,password));
	    }
	    
	    @PostMapping("/forgot-password")
	    public ResponseEntity<ApiResponse> forgotPassword(
	    		@RequestParam 
	            @NotBlank(message = "Email cannot be blank") 
	            @Email(message = "Invalid email format")String email) {
	    	return  ResponseEntity.ok(service.forgotPassword(email));
	        
	    }

	    @PostMapping("/reset-password")
	    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
//	        farmerService.resetPassword(request);
	        return  ResponseEntity.ok(service.resetPassword(request));
	    }
	    
	    @GetMapping("/getall")
	    public ResponseEntity<ApiResponse> getAll() {
	        return ResponseEntity.ok(service.getAllPersons());
	    }

	    @GetMapping("/getbyid")
	    public ResponseEntity<ApiResponse> get(@NotNull(message = "Delivery_Boy ID cannot be null") @RequestParam @Positive(message = "ID must be positive")  Long Id) {
	        return ResponseEntity.ok(service.getById(Id));
	    }

	    @GetMapping("/get")
	    public ResponseEntity<ApiResponse> get() {
	        return ResponseEntity.ok(service.getDelivery_boy());
	    }
	    
	    @PutMapping("/update")
	    public ResponseEntity<ApiResponse> update(@Valid @RequestBody DeliveryPersonDTO updatedDto) {
	        return ResponseEntity.ok(service.update(updatedDto));
	    }

	    @DeleteMapping("/delete")
	    public ResponseEntity<ApiResponse> softDelete(  @NotBlank(message = "Email cannot be blank") 
	    @Email(message = "Invalid email format")@RequestParam String email) {
	        return ResponseEntity.ok(service.softDelete(email));
	    }
	    
	    @PutMapping("/activate")
	    public ResponseEntity<ApiResponse> activate( @NotBlank(message = "Email cannot be blank") 
	    @Email(message = "Invalid email format")@RequestParam String email) {
	        return ResponseEntity.ok(service.activate(email));
	    }
	    
	    
	    @GetMapping("/getavailable")
	    public ResponseEntity<ApiResponse> getAvailablePersons() {
	        return ResponseEntity.ok(service.getAvailablePersons());
	    }
	    
	 
	    
	    @PutMapping("/updateStatus")
	    public ResponseEntity<ApiResponse> updateStatus(
	    		
	    @NotNull(message = "deliveryPerson ID cannot be null")
	    @RequestParam @Positive(message = "ID must be positive")  Long id,  
        @RequestParam
        @NotNull(message = "Ststus cannot be blank") 
        Status status) {
	        return ResponseEntity.ok(service.updateStatus(id,status));
	    }
}
