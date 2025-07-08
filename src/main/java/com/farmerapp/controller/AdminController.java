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

import com.farmerapp.payload.AdminDto;
import com.farmerapp.payload.ResetPasswordRequest;
import com.farmerapp.request.AdminUpdateRequest;
import com.farmerapp.response.ApiResponse;
import com.farmerapp.service.AdminService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/admin")
@Validated
public class AdminController {


			@Autowired
			private AdminService adminService;
			
			@PostMapping("/register")
		    public ResponseEntity<ApiResponse> registerAdmin(@Valid @RequestBody AdminDto admin) {
		    	 	return ResponseEntity.ok(adminService.registerAdmin(admin));
		    }

		    @PostMapping("/verify-otp")
		    public  ResponseEntity<ApiResponse> verifyadminOtp(@RequestParam @NotBlank(message = "OTP cannot be blank") 
    		@Pattern(regexp = "\\d+", message = "OTP must be numeric") 
            @Size(min = 4, max = 6, message = "OTP must be 4 to 6 digits")   String otp) {
		        return ResponseEntity.ok(adminService.verifyadminOtp(otp));
		    }
		    
		    @PostMapping("/login")
		    public ResponseEntity<ApiResponse> loginAdmin(@RequestParam("email") 
            @NotBlank(message = "Email cannot be blank") 
            @Email(message = "Invalid email format") String email,  
            @RequestParam("password") 
            @NotBlank(message = "Password cannot be blank") 
            @Size(min = 6, message = "Password must be at least 6 characters")String password ) {
				return  ResponseEntity.ok(adminService.loginAdmin(email,password));
		    }

		    @PostMapping("/forgot-password")
		    public ResponseEntity<ApiResponse> forgotPassword(@RequestParam 
		            @NotBlank(message = "Email cannot be blank") 
		            @Email(message = "Invalid email format") String email) {
		    	return  ResponseEntity.ok(adminService.forgotPassword(email));
		        
		    }

		    @PostMapping("/reset-password")
		    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
//		        consumerService.resetPassword(request);
		        return  ResponseEntity.ok(adminService.resetPassword(request));
		    }
		    
		    @GetMapping("/getbyid")
		    public ResponseEntity<ApiResponse> getAdminById(@NotNull(message = "Admin ID cannot be null") @RequestParam @Positive(message = "ID must be positive") Long id) {
		        return ResponseEntity.ok(adminService.getAdminById(id));
		    }
		    
		    @GetMapping("/getadmin")
		    public ResponseEntity<ApiResponse> getConsumer() {
		        return ResponseEntity.ok(adminService.getAdmin());
		    }

		    @GetMapping("/getall")
		    public ResponseEntity<ApiResponse> getAllAdmins() {
		        return ResponseEntity.ok(adminService.getAllAdmins());
		    }

		    @PutMapping("/update")
		    public ResponseEntity<ApiResponse> updateAdmin( @Valid @RequestBody AdminUpdateRequest adminUpdateRequest) {
		        return ResponseEntity.ok(adminService.updateAdmin(adminUpdateRequest));
		    }

		    @DeleteMapping("/delete")
		    public ResponseEntity<ApiResponse> deleteAdmin(@NotBlank(message = "Email cannot be blank") 
		    @Email(message = "Invalid email format")@RequestParam String email) {
		        return ResponseEntity.ok(adminService.softDeleteAdmin(email));
		    }

		    @PutMapping("/activate")
		    public ResponseEntity<ApiResponse> activateAdmin(@NotBlank(message = "Email cannot be blank") 
		    @Email(message = "Invalid email format")@RequestParam String email) {
		        return ResponseEntity.ok(adminService.activateAdmin(email));
		    }
		    
		    @PutMapping("/updatefarmerstatus")
		    public ResponseEntity<ApiResponse> updateFarmerStatus(
		    		@NotNull(message = "Farmer ID cannot be null") @RequestParam @Positive(message = "ID must be positive") Long farmerId,
		    		@NotNull(message = "Status cannot be blank") @RequestParam boolean status ) {
		        return ResponseEntity.ok(adminService.updateFarmerStatus(farmerId,status));
		    }
		    
		    @PutMapping("/updateconsumerstatus")
		    public ResponseEntity<ApiResponse> updateConsumerStatus(
		    		@NotNull(message = "Consumer ID cannot be null") @RequestParam @Positive(message = "ID must be positive") Long consumerId,
		    		@NotNull(message = "Status cannot be blank") @RequestParam boolean status ) {
		        return ResponseEntity.ok(adminService.updateConsumerStatus(consumerId,status));
		    }
		    
		    
	
}
