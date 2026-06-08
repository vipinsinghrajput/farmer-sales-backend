package com.farmerapp.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.farmerapp.entity.Admin;
import com.farmerapp.entity.Farmer;
import com.farmerapp.entity.Notification;
import com.farmerapp.entity.Otp;
import com.farmerapp.entity.OtpType;
import com.farmerapp.entity.TokenType;
import com.farmerapp.entity.UnverifiedFarmer;
import com.farmerapp.exception.AccountDeactivatedException;
import com.farmerapp.exception.InvalidPasswordException;
import com.farmerapp.exception.OtpExpiredException;
import com.farmerapp.exception.OtpNotVerifiedException;
import com.farmerapp.exception.UserAlreadyExistsException;
import com.farmerapp.exception.UserNotFoundException;
import com.farmerapp.exception.farmerAlreadyExistsException;
import com.farmerapp.payload.FarmerDto;
import com.farmerapp.payload.ResetPasswordRequest;
import com.farmerapp.repository.AdminRepository;
import com.farmerapp.repository.FarmerRepository;
import com.farmerapp.repository.NotificationRepository;
import com.farmerapp.repository.OtpRepository;
import com.farmerapp.repository.UnverifiedFarmerRepository;
import com.farmerapp.request.farmerUpdateRequest;
import com.farmerapp.response.ApiResponse;
import com.farmerapp.specification.FarmerSpecification;
import com.farmerapp.util.AppUtils;
import com.farmerapp.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FarmerService{
    @Autowired 
    private FarmerRepository farmerRepository;
    @Autowired 
    private UnverifiedFarmerRepository UnverifiedFarmerRepository;
    @Autowired 
    private PasswordEncoder passwordEncoder;
    @Autowired 
    private JwtUtil jwtUtil;
    @Autowired 
    private OtpRepository otpRepository;
    @Autowired 
    private AppUtils appUtils;
    @Autowired
    private EmailService emailService;
    @Autowired
    private GeocodingService geocodingService;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private NotificationRepository notificationRepository;
   
    public Page<Farmer> filterFarmers(Long id, String name, Boolean status, int page, int size) {
        Specification<Farmer> spec = Specification
                .where(FarmerSpecification.hasId(id))
                .and(FarmerSpecification.hasName(name))
                .and(FarmerSpecification.hasStatus(status));

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return farmerRepository.findAll(spec, pageable);
    }
    
    
    
    public ApiResponse registerFarmer(FarmerDto farmer) {
        if (farmerRepository.existsByEmail(farmer.getEmail())) {
//            return ApiResponse.builder().message("Farmer Already Exists").response(farmer.getEmail()).build();
                    	throw new UserAlreadyExistsException("Farmer Already Exists");
        }

        if(farmerRepository.existsBymobileNumber(farmer.getMobileNumber())) {
        	throw new UserAlreadyExistsException("Duplicate entry : " + farmer.getMobileNumber());
        }
//        String additionalData = convertToJson(farmer);
        
        UnverifiedFarmer unverifiedfarmer = UnverifiedFarmer.builder()
        	    .name(farmer.getName())
        	    .mobileNumber(farmer.getMobileNumber())
        	    .email(farmer.getEmail())
        	    .password(passwordEncoder.encode(farmer.getPassword()))
        	    .farmName(farmer.getFarmName())
        	    .farmAddress(farmer.getFarmAddress())
        	    .farmCity(farmer.getFarmCity())
        	    .farmState(farmer.getFarmState())
        	    .farmCountry(farmer.getFarmCountry())
        	    .pincode(farmer.getPincode())
        	    .farmDescription(farmer.getFarmDescription())
        	    .farmLicenseNumber(farmer.getFarmLicenseNumber())
        	    .build();
       
        UnverifiedFarmerRepository.save(unverifiedfarmer);

        Map <String, Object> response = new HashMap<String, Object>();
        String token = jwtUtil.generateToken(farmer.getEmail(), OtpType.REGISTER, TokenType.AUTH_TOKEN,"FARMER");
        response.put("token", token);
        response.put("otp", emailService.sendOtp(farmer.getEmail(), OtpType.REGISTER));

        return ApiResponse.builder().message("Verify your email").response(response).build();
    }

    private String formatFarmAddress(UnverifiedFarmer userRequest) {
		return String.format("%s, %s, %s, %s, %s", userRequest.getFarmAddress().trim(), userRequest.getFarmCity().trim(),
				userRequest.getFarmState().trim(), userRequest.getPincode().trim(), userRequest.getFarmCountry().trim());
	}
    
    public ApiResponse verifyfarmerOtp(String otp) {
       
    	String token = appUtils.getTokenFromHeader();
    	System.err.println("Token =====  " +token);
    	Map<String , Object>res = new HashMap<>();
//		if(token!=null)
	String email = jwtUtil.getUsername(token);

	Otp dbOtp = otpRepository.findByEmail(email);
	if (dbOtp != null ) {
		
		if (TokenType.AUTH_TOKEN.toString().equals((String) jwtUtil.getHeader(token, "tokenType"))  && dbOtp.getOtpCode().equals(otp)) {
			if (dbOtp.getExpirationTime().isAfter(LocalDateTime.now()) ) {
				
				if ("REGISTER".toString().equals(jwtUtil.getHeader(token, "otpType"))) {
					
					UnverifiedFarmer unverifiedfarmer = UnverifiedFarmerRepository.findByEmail(email);
					
					
					
//					 UserDto user = convertToUser(unvu);
					
					 Farmer farmer = new Farmer();
					 
					  String fullAddress = unverifiedfarmer.getFarmAddress();

						double[] latLon = geocodingService.fetchCoordinatesWithFallback(formatFarmAddress(unverifiedfarmer),
								unverifiedfarmer.getFarmCity(),unverifiedfarmer.getFarmState(),unverifiedfarmer.getFarmCountry(), unverifiedfarmer.getPincode());
						farmer.setLatitude(latLon[0]);
						farmer.setLongitude(latLon[1]);

				        farmer.setName(unverifiedfarmer.getName());  
				        farmer.setEmail(unverifiedfarmer.getEmail());
				        farmer.setMobileNumber(unverifiedfarmer.getMobileNumber());
				        farmer.setPassword(unverifiedfarmer.getPassword());
				        farmer.setFarmName(unverifiedfarmer.getFarmName());
				        farmer.setFarmAddress(unverifiedfarmer.getFarmAddress());
				        farmer.setFarmCity(unverifiedfarmer.getFarmCity());
				        farmer.setFarmState(unverifiedfarmer.getFarmState());
				        farmer.setFarmCountry(unverifiedfarmer.getFarmCountry());
				        farmer.setPincode(unverifiedfarmer.getPincode());
				        farmer.setFarmDescription(unverifiedfarmer.getFarmDescription());
				        farmer.setFarmLicenseNumber(unverifiedfarmer.getFarmLicenseNumber());

				        farmerRepository.save(farmer);
				        UnverifiedFarmerRepository.delete(unverifiedfarmer);
					    // Clean up temporary storage
				  
//					 dbOtp.setExpirationTime(LocalDateTime.now());   
				     otpRepository.delete(dbOtp);
				     
					String accessToken = jwtUtil.generateToken(email, OtpType.REGISTER, TokenType.ACCESS_TOKEN,"FARMER");
					
					res.put("accessToken", accessToken);
					
					// ✅ Send notification to admin
				    Admin admin = adminRepository.findByEmail("amitsharma@example.com").orElse(null); // or get by email if you have a single admin
				  
				    Notification notification = Notification.builder()
				        .admin(admin)
				        .message("A new farmer account has been verified: " + farmer.getName())
				        .isRead(false)
				        .build();
				    
				    notificationRepository.save(notification);
				    
					return  ApiResponse.builder().message("User successfully verify").response(res).build();
					
				} else if ("LOGIN".toString().equals(jwtUtil.getHeader(token, "otpType"))) {
					dbOtp.setExpirationTime(LocalDateTime.now());          
					otpRepository.delete(dbOtp);
					String accessToken = jwtUtil.generateToken(email, OtpType.LOGIN, TokenType.ACCESS_TOKEN,"FARMER");
					res.put("accessToken", accessToken);
					return	ApiResponse.builder().message("User successfully verify & Login").response(res).build();
				}
//			return	ApiResponse.builder().message("Not match OTP_TYPE").build();
				throw new OtpNotVerifiedException("Not match OTP_TYPE");
			}
//			return	ApiResponse.builder().message("Otp expired").build();
			throw new OtpExpiredException("Otp expired");
		}
//		  return ApiResponse.builder().message("Invalid OTP").build();
		throw new OtpNotVerifiedException("Invalid OTP");
	}
//	return ApiResponse.builder().message("User Not Found ").build();
	throw new UserNotFoundException("User Not Found " + email);

}
    	
    	

    public ApiResponse loginFarmer(String email, String password) {

    	if (farmerRepository.existsByEmail(email)) {
//			if (farmerRepository.existsByEmailAndPassword(email, password)) {
    	
    	   Farmer farmer = farmerRepository.findByEmail(email)
    	            .orElseThrow(() -> new UserNotFoundException("User Not Found " + email));
    	   
    	   if (!farmer.isStatus()) {
   	        throw new AccountDeactivatedException("Account is deactivated. Please contact support.");
   	    }
    	    if (!passwordEncoder.matches(password, farmer.getPassword())) {
//    	       return ApiResponse.builder().message("Invalid Password").build();
    	    	throw new InvalidPasswordException("Invalid Password");
    	    }
    	
				Map<String, Object> response = new HashMap<>();
				String token = jwtUtil.generateToken(email, OtpType.LOGIN, TokenType.AUTH_TOKEN,"FARMER");
//					userRepo.getByEmailAndPassword(email, password);
				response.put("token", token);
				response.put("otp", emailService.sendOtp(email, OtpType.LOGIN));

				return ApiResponse.builder().message("Verify you email")
						.response(response).build();
//			} else
//				throw new InvalidPasswordException("invalid Password");
		} else
//			return ApiResponse.builder().message("User Not Found").response(email).build();
	throw new UserNotFoundException("User Not Found " + email);

    	 }
    
    
    public ApiResponse forgotPassword(String email) {
      
    	if (farmerRepository.existsByEmail(email)) {
    		
    	
    	Farmer farmer = farmerRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Farmer not found with email: " + email));

    	 if (!farmer.isStatus()) {
    	        throw new AccountDeactivatedException("Account is deactivated. Please contact support.");
    	    }
    	
        Map <String, Object> response = new HashMap<String, Object>();
        String token = jwtUtil.generateToken(farmer.getEmail(), OtpType.FORGET_PASSWORD, TokenType.AUTH_TOKEN,"FARMER");
        response.put("token", token);
        response.put("otp", emailService.sendOtp(farmer.getEmail(), OtpType.REGISTER));

        return ApiResponse.builder().message("Reset your password").response(response).build();
    }else
//    	return ApiResponse.builder().message("Farmer Not Found").response(email).build();
    	throw new UserNotFoundException("Farmer Not Found" + email);
 }
  
   
    public ApiResponse resetPassword(ResetPasswordRequest request) {
        
    	String token = appUtils.getTokenFromHeader();
//		if(token!=null)
	    String email = jwtUtil.getUsername(token);

	Otp dbOtp = otpRepository.findByEmail(email);
	if (dbOtp != null) {
		if (TokenType.AUTH_TOKEN.toString().equals((String) jwtUtil.getHeader(token, "tokenType"))  && dbOtp.getOtpCode().equals(request.getOtp())) {
			if (dbOtp.getExpirationTime().isAfter(LocalDateTime.now()) ) {
				
				if ("FORGET_PASSWORD".toString().equals(jwtUtil.getHeader(token, "otpType"))) {	

					if(farmerRepository.existsByEmail(email)) {
					
					
                Farmer farmer = farmerRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Farmer not found"));
                
                if (!farmer.isStatus()) {
           	        throw new AccountDeactivatedException("Account is deactivated. Please contact support.");
           	    }

                String encodedPassword = passwordEncoder.encode(request.getNewPassword());
                farmerRepository.updatePasswordByEmail(email, encodedPassword);
                otpRepository.delete(dbOtp);

//                System.out.println("Updating Farmer password for email: " + email);
//                System.out.println("New password (raw): " + request.getNewPassword());
//                System.out.println("New password (encoded): " + encodedPassword);

//        farmer.setPassword(passwordEncoder.encode(request.getNewPassword()));
//        farmerRepository.save(farmer);
//        otpRepository.delete(dbOtp);
        
        return ApiResponse.builder().message("Password reset  successfully ").response(email).build();
				}else
//					return	ApiResponse.builder().message("Farmer Not Found").build();
					throw new UserNotFoundException("Farmer Not Found");
					}
//				return	ApiResponse.builder().message("Not match OTP_TYPE").build();
				throw new OtpNotVerifiedException("Not match OTP_TYPE");
			}
//			return	ApiResponse.builder().message("Otp expired").build();
			throw new OtpExpiredException("Otp expired");
		}
//		  return ApiResponse.builder().message("Invalid OTP").build();
		throw new OtpNotVerifiedException("Invalid OTP");
	}
//	return ApiResponse.builder().message("User Not Found ").build();
	throw new UserNotFoundException("User Not Found " + email);
    }
    
    

    
    public ApiResponse getFarmerById(Long farmerId) {
        
    	Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new UserNotFoundException("Farmer Not Found: " + farmerId));

        return ApiResponse.builder()
                .message("Farmer details fetched successfully")
                .response(farmer)
                .build();
    }

 public ApiResponse getFarmer() {
	 
	    String token = appUtils.getTokenFromHeader();
	    String email = jwtUtil.getUsername(token);
	    
	
    	Farmer farmer = farmerRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Farmer Not Found: "));

        return ApiResponse.builder()
                .message("Farmer details fetched successfully")
                .response(farmer)
                .build();
    }

   
    
//    public ApiResponse getAllFarmers() {
//      
//    	List<Farmer> farmers = farmerRepository.findAll();
//    	
//        return ApiResponse.builder()
//                .message("Farmers fetched successfully")
//                .response(farmers)
//                .build();
//    }
    
    
    public ApiResponse getAllFarmers(Long id, String name, Boolean status, int page, int size) {
      
    	Specification<Farmer> spec = Specification
                .where(FarmerSpecification.hasId(id))
                .and(FarmerSpecification.hasName(name))
                .and(FarmerSpecification.hasStatus(status));

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
      
        Page<Farmer> farmerPage = farmerRepository.findAll(spec, pageable);

        
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("farmers", farmerPage.getContent());
        responseMap.put("totalElements", farmerPage.getTotalElements());
        responseMap.put("totalPages", farmerPage.getTotalPages());
        responseMap.put("currentPage", farmerPage.getNumber());
        responseMap.put("pageSize", farmerPage.getSize());
        
        return ApiResponse.builder()
                .message("Farmers fetched successfully")
                .response(responseMap)
                .build();
    }
    
    
    public ApiResponse updateFarmer(farmerUpdateRequest updatedFarmer) {
       
    	String token = appUtils.getTokenFromHeader();
//		if(token!=null)
	    String email = jwtUtil.getUsername(token);
	    
    	Farmer farmer = farmerRepository.findByEmailAndStatus(email, true)
                .orElseThrow(() -> new UserNotFoundException("Farmer Not Found: " + email));

//    	 if(farmerRepository.existsBymobileNumber(updatedFarmer.getMobileNumber())) {
//         	throw new UserAlreadyExistsException("Duplicate entry : " + updatedFarmer.getMobileNumber() );
//         }
//    	
    	 if (updatedFarmer.getName() != null) {
    	        farmer.setName(updatedFarmer.getName());
    	    }
//    	    if (updatedFarmer.getMobileNumber() != null) {
//    	        farmer.setMobileNumber(updatedFarmer.getMobileNumber());
//    	    }
    	    if (updatedFarmer.getFarmName() != null) {
    	        farmer.setFarmName(updatedFarmer.getFarmName());
    	    }
    	    if (updatedFarmer.getFarmAddress() != null) {
    	    	farmer.setFarmAddress(updatedFarmer.getFarmAddress());
    	    	
    	    }
    	    if (updatedFarmer.getFarmCity() != null) {
    	    	farmer.setFarmCity(updatedFarmer.getFarmCity());
    	    }
    	    if (updatedFarmer.getFarmState() != null) {
    	    	farmer.setFarmState(updatedFarmer.getFarmState());
    	    }
    	    if (updatedFarmer.getFarmCountry() != null) {
    	    	farmer.setFarmCountry(updatedFarmer.getFarmCountry());
    	    }
    	    if (updatedFarmer.getPincode() != null) {
    	        farmer.setPincode(updatedFarmer.getPincode());
    	    }
    	    if (updatedFarmer.getFarmDescription() != null) {
    	    	farmer.setFarmDescription(updatedFarmer.getFarmDescription());
    	    }
    	    if (updatedFarmer.getFarmLicenseNumber() != null) {
    	    	farmer.setFarmLicenseNumber(updatedFarmer.getFarmLicenseNumber());
    	    }
       
    	    double[] latLon = geocodingService.fetchCoordinatesWithFallback(farmer.getFarmAddress(),
					farmer.getFarmCity(),farmer.getFarmState(),farmer.getFarmCountry(), farmer.getPincode());
			farmer.setLatitude(latLon[0]);
			farmer.setLongitude(latLon[1]);
    	    
    	    farmerRepository.save(farmer);

          return ApiResponse.builder()
                  .message("Farmer details updated successfully")
                  .response(farmer)
                  .build();
          
    }

    
    public ApiResponse softDeleteFarmer(String email) {
    	 Farmer farmer = farmerRepository.findByEmailAndStatus(email, true)
                 .orElseThrow(() -> new UserNotFoundException("Farmer Not Found: " + email));

         farmer.setStatus(false);
         farmerRepository.save(farmer);

         return ApiResponse.builder()
                 .message("Farmer account deactivated successfully")
                 .response(email)
                 .build();
     }
    
    
    public ApiResponse activateFarmer(String email) {
        Farmer farmer = farmerRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User Not Found: " + email));

        if(farmer.isStatus())
        	return ApiResponse.builder().message("Account allready activated ").response(email).build();
       
        farmer.setStatus(true);
        farmerRepository.save(farmer);

        return ApiResponse.builder().message("Account activated successfully").response(email).build();
    }
    
    
   

   

    
    
    
//    private String convertToJson(FarmerDto user) {
//		try {
//			return objectMapper.writeValueAsString(user);
//		} catch (JsonProcessingException e) {
//			throw new RuntimeException("Error converting user data", e);
//		}
//
//	}
//
//	private UserDto convertToUser(UnverifiedUser unverifiedUser) {
//		try {
//			UserDto user = objectMapper.readValue(unverifiedUser.getAdditionalData(), UserDto.class);
////			user.setId(null); // Avoid ID conflict
//			return user;
//		} catch (JsonProcessingException e) {
//			throw new RuntimeException("Error converting JSON to user", e);
//		}
//	}
	
}

