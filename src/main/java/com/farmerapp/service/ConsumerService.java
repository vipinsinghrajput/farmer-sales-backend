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

import com.farmerapp.entity.Consumer;
import com.farmerapp.entity.Farmer;
import com.farmerapp.entity.Otp;
import com.farmerapp.entity.OtpType;
import com.farmerapp.entity.TokenType;
import com.farmerapp.entity.UnverifiedConsumer;
import com.farmerapp.exception.AccountDeactivatedException;
import com.farmerapp.exception.InvalidPasswordException;
import com.farmerapp.exception.OtpExpiredException;
import com.farmerapp.exception.OtpNotVerifiedException;
import com.farmerapp.exception.UserAlreadyExistsException;
import com.farmerapp.exception.UserNotFoundException;
import com.farmerapp.exception.farmerAlreadyExistsException;
import com.farmerapp.payload.ConsumerDto;
import com.farmerapp.payload.FarmerDto;
import com.farmerapp.payload.ResetPasswordRequest;
import com.farmerapp.repository.ConsumerRepository;
import com.farmerapp.repository.OtpRepository;
import com.farmerapp.repository.UnverifiedConsumerRepository;
import com.farmerapp.request.consumerUpdateRequest;
import com.farmerapp.response.ApiResponse;
import com.farmerapp.specification.ConsumerSpecification;
import com.farmerapp.util.AppUtils;
import com.farmerapp.util.JwtUtil;

@Service
public class ConsumerService {

	 @Autowired 
	    private ConsumerRepository consumerRepository;
	    @Autowired 
	    private UnverifiedConsumerRepository unverifiedconsumerRepository;
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
	    
	    public ApiResponse registerConsumer(ConsumerDto consumer) {
	        if (consumerRepository.existsByEmail(consumer.getEmail())) {
//	           return ApiResponse.builder().message("Consumer Already Exists").response(consumer.getEmail()).build();
	        	throw new UserAlreadyExistsException("Consumer Already Exists");
	        }
	        
	        if(consumerRepository.existsByMobileNumber(consumer.getMobileNumber()))
	        	throw new UserAlreadyExistsException(" Duplicate entry "+consumer.getMobileNumber());
//	        String additionalData = convertToJson(farmer);
	        
	        UnverifiedConsumer unverifiedconsumer = UnverifiedConsumer.builder()
	        		 .name(consumer.getName())
	        	        .mobileNumber(consumer.getMobileNumber())
	        	        .email(consumer.getEmail())
	        	        .password(passwordEncoder.encode(consumer.getPassword()))
	        	        .deliveryAddress(consumer.getDeliveryAddress()) // Mapping farmAddress as deliveryAddress
	        	        .pincode(consumer.getPincode()) 
	        	        .build();
	       
	        unverifiedconsumerRepository.save(unverifiedconsumer);

	        Map <String, Object> response = new HashMap<String, Object>();
	        String token = jwtUtil.generateToken(consumer.getEmail(), OtpType.REGISTER, TokenType.AUTH_TOKEN,"CONSUMER");
	        response.put("token", token);
	        response.put("otp", emailService.sendOtp(consumer.getEmail(), OtpType.REGISTER));

	        return ApiResponse.builder().message("Verify your email").response(response).build();
	    }

	    public ApiResponse verifyconsumerOtp(String otp) {
	       
	    	String token = appUtils.getTokenFromHeader();
//			if(token!=null)
		String email = jwtUtil.getUsername(token);
		Map<String , Object>res = new HashMap<>();
		Otp dbOtp = otpRepository.findByEmail(email);
		
		if (dbOtp != null) {
			if (TokenType.AUTH_TOKEN.toString().equals((String) jwtUtil.getHeader(token, "tokenType"))  && dbOtp.getOtpCode().equals(otp)) {
				if (dbOtp.getExpirationTime().isAfter(LocalDateTime.now())  ) {
					
					if ("REGISTER".toString().equals(jwtUtil.getHeader(token, "otpType"))) {
						
						UnverifiedConsumer unverifiedconsumer = unverifiedconsumerRepository.findByEmail(email);
						
									
//						 UserDto user = convertToUser(unvu);
						
						Consumer consumer = new Consumer();
						consumer.setName(unverifiedconsumer.getName());  
						consumer.setEmail(unverifiedconsumer.getEmail());
						consumer.setMobileNumber(unverifiedconsumer.getMobileNumber());
						consumer.setPassword((unverifiedconsumer.getPassword())); 
						consumer.setDeliveryAddress(unverifiedconsumer.getDeliveryAddress());
						consumer.setPincode(unverifiedconsumer.getPincode());

					        consumerRepository.save(consumer);
					        unverifiedconsumerRepository.delete(unverifiedconsumer);
						    // Clean up temporary storage
						    
						    
//						 dbOtp.setExpirationTime(LocalDateTime.now());   
					     otpRepository.delete(dbOtp);
					
					     
						String accessToken = jwtUtil.generateToken(email, OtpType.REGISTER, TokenType.ACCESS_TOKEN,"CONSUMER");
						res.put("accessToken", accessToken);
						return  ApiResponse.builder().message("User successfully verify").response(res).build();
						
					} else if ("LOGIN".toString().equals(jwtUtil.getHeader(token, "otpType"))) {
						dbOtp.setExpirationTime(LocalDateTime.now());
						 otpRepository.delete(dbOtp);
						String accessToken = jwtUtil.generateToken(email, OtpType.LOGIN, TokenType.ACCESS_TOKEN,"CONSUMER");
						res.put("accessToken", accessToken);
						return	ApiResponse.builder().message("User successfully verify & Login").response(res).build();
					}
//					return	ApiResponse.builder().message("Not match OTP_TYPE").build();
					throw new OtpNotVerifiedException("Not match OTP_TYPE");
				}
//				return	ApiResponse.builder().message("Otp expired").build();
				throw new OtpExpiredException("Otp expired");
			}
//			  return ApiResponse.builder().message("Invalid OTP").build();
			throw new OtpNotVerifiedException("Invalid OTP");
		}
//		return ApiResponse.builder().message("User Not Found ").build();
		throw new UserNotFoundException("User Not Found " + email);

	}
	    	
	    	

	    public ApiResponse loginConsumer(String email, String password) {

	    	if (consumerRepository.existsByEmail(email)) {
	    	
	    	   Consumer consumer = consumerRepository.findByEmail(email)
	    	            .orElseThrow(() -> new UserNotFoundException("User Not Found " + email));

	    	   if (!consumer.isStatus()) {
	    	        throw new AccountDeactivatedException("Account is deactivated. Please contact support.");
	    	    }
	  
	    	    if (!passwordEncoder.matches(password, consumer.getPassword())) {
//	    	    	return ApiResponse.builder().message("Invalid Password").build();
	    	    	throw new InvalidPasswordException("Invalid Password");
	    	    }
	    	
					Map<String, Object> response = new HashMap<>();
					String token = jwtUtil.generateToken(email, OtpType.LOGIN, TokenType.AUTH_TOKEN,"CONSUMER");
//						userRepo.getByEmailAndPassword(email, password);
					response.put("token", token);
					response.put("otp",  emailService.sendOtp(email, OtpType.LOGIN));

					return ApiResponse.builder().message("Verify your email")
							.response(response).build();
	    	} else
//				return ApiResponse.builder().message("User Not Found").response(email).build();
	    	throw new UserNotFoundException("User Not Found " + email);
	    }
	    
	    
	    public ApiResponse forgotPassword(String email) {
	       
	    	
	    	if (consumerRepository.existsByEmail(email)) {
	    		
	    	Consumer  consumer =consumerRepository.findByEmail(email)
	                .orElseThrow(() -> new UserNotFoundException(" consumer not found with email : " + email));
	    	
	    	if (!consumer.isStatus()) {
	    	        throw new AccountDeactivatedException("Account is deactivated. Please contact support.");
	    	    }

	        Map <String, Object> response = new HashMap<String, Object>();
	        String token = jwtUtil.generateToken(consumer.getEmail(), OtpType.FORGET_PASSWORD, TokenType.AUTH_TOKEN,"CONSUMER");
	        response.put("token", token);
	        response.put("otp",  emailService.sendOtp(consumer.getEmail(), OtpType.FORGET_PASSWORD));

	        return ApiResponse.builder().message("Reset your password").response(response).build();
	    }
	    else
//	    	return ApiResponse.builder().message("Farmer Not Found").response(email).build();
	    	throw new UserNotFoundException("Consumer Not Found  : " + email);
}
	   
	    
	    public ApiResponse resetPassword(ResetPasswordRequest request) {
	        
	    	String token = appUtils.getTokenFromHeader();
//			if(token!=null)
		    String email = jwtUtil.getUsername(token);

		Otp dbOtp = otpRepository.findByEmail(email);
		if (dbOtp != null) {
			if (TokenType.AUTH_TOKEN.toString().equals((String) jwtUtil.getHeader(token, "tokenType"))  && dbOtp.getOtpCode().equals(request.getOtp())) {
				if (dbOtp.getExpirationTime().isAfter(LocalDateTime.now())  ) {
					
					if ("FORGET_PASSWORD".toString().equals(jwtUtil.getHeader(token, "otpType"))) {	
	    	     
						if(consumerRepository.existsByEmail(email)) {
	        Consumer consumer = consumerRepository.findByEmail(email)
	                .orElseThrow(() -> new UserNotFoundException("Consumer not found"));
	       
	        if (!consumer.isStatus()) {
    	        throw new AccountDeactivatedException("Account is deactivated. Please contact support.");
    	    }

	        consumer.setPassword(passwordEncoder.encode(request.getNewPassword()));
	        consumerRepository.save(consumer);
	        otpRepository.delete(dbOtp);
	        
	        return ApiResponse.builder().message("Password reset  successfully ").response(email).build();
					}
					else
//						return	ApiResponse.builder().message("Farmer Not Found").build();
						throw new UserNotFoundException("consummer Not Found");
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
	throw new UserNotFoundException("User Not Found ");

	    }
	    
	    
	    public ApiResponse getConsumerById(Long consumerId) {
	        
	    	Consumer consumer = consumerRepository.findById(consumerId)
	                .orElseThrow(() -> new UserNotFoundException("Consumer Not Found: " + consumerId));

	        return ApiResponse.builder()
	                .message("Consumer details fetched successfully")
	                .response(consumer)
	                .build();
	    }

	    public ApiResponse getConsumer() {
	   	 
		    String token = appUtils.getTokenFromHeader();
		    String email = jwtUtil.getUsername(token);
		    
		
	    	Consumer consumer = consumerRepository.findByEmail(email)
	                .orElseThrow(() -> new UserNotFoundException("Consumer Not Found: "));

	        return ApiResponse.builder()
	                .message("Consumer details fetched successfully")
	                .response(consumer)
	                .build();
	    }
	    
//	    public ApiResponse getAllConsumers() {
//	        List<Consumer> consumers = consumerRepository.findAll();
//	        return ApiResponse.builder()
//	                .message("Consumers fetched successfully")
//	                .response(consumers)
//	                .build();
//	    }
	    
	    
	    public ApiResponse getAllConsumers(Long id, String name, Boolean status, int page, int size) {
	        Specification<Consumer> spec = Specification
	                .where(ConsumerSpecification.hasId(id))
	                .and(ConsumerSpecification.hasName(name))
	                .and(ConsumerSpecification.hasStatus(status));

	        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
	        Page<Consumer> consumerPage = consumerRepository.findAll(spec, pageable);

	        Map<String, Object> responseMap = new HashMap<>();
	        responseMap.put("consumers", consumerPage.getContent());
	        responseMap.put("totalPages", consumerPage.getTotalPages());
	        responseMap.put("totalElements", consumerPage.getTotalElements());
	        responseMap.put("currentPage", consumerPage.getNumber());

	        return ApiResponse.builder()
	                .message("Consumers fetched successfully")
	                .response(responseMap)
	                .build();
	    }

	    
	    public ApiResponse updateConsumer(consumerUpdateRequest updatedConsumer) {
	       
	    	
	    	Consumer consumer = consumerRepository.findByEmailAndStatus(updatedConsumer.getEmail(), true)
	                .orElseThrow(() -> new UserNotFoundException("Consumer Not Found: " + updatedConsumer.getEmail()));

	    	 if (updatedConsumer.getName() != null) {
	    	        consumer.setName(updatedConsumer.getName());
	    	    }
//	    	    if (updatedConsumer.getMobileNumber() != null) {
//	    	        consumer.setMobileNumber(updatedConsumer.getMobileNumber());
//	    	    }
	    	    if (updatedConsumer.getDeliveryAddress() != null) {
	    	        consumer.setDeliveryAddress(updatedConsumer.getDeliveryAddress());
	    	    }
	    	    if (updatedConsumer.getPincode() != null) {
	    	        consumer.setPincode(updatedConsumer.getPincode());
	    	    }
	       
	    	    consumerRepository.save(consumer);

	          return ApiResponse.builder()
	                  .message("Consumer details updated successfully")
	                  .response(consumer)
	                  .build();
	          
	    }

	    
	    public ApiResponse softDeleteConsumer(String email) {
	    	 Consumer consumer = consumerRepository.findByEmailAndStatus(email, true)
	                 .orElseThrow(() -> new UserNotFoundException("Consumer Not Found: " + email));

	         consumer.setStatus(false);
	         consumerRepository.save(consumer);

	         return ApiResponse.builder()
	                 .message("Consumer account deactivated successfully")
	                 .response(email)
	                 .build();
	     }
	    
	    
	    public ApiResponse activateConsumer(String email) {
	        Consumer consumer = consumerRepository.findByEmail(email)
	                .orElseThrow(() -> new UserNotFoundException("User Not Found: " + email));

	        if(consumer.isStatus())
	        	return ApiResponse.builder().message("Account allready activated ").response(email).build();
	       
	        consumer.setStatus(true);
	        consumerRepository.save(consumer);

	        return ApiResponse.builder().message("Account activated successfully").response(email).build();
	    }



}
