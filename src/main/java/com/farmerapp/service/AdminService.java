package com.farmerapp.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.farmerapp.entity.Admin;
import com.farmerapp.entity.Consumer;
import com.farmerapp.entity.Farmer;
import com.farmerapp.entity.Otp;
import com.farmerapp.entity.OtpType;
import com.farmerapp.entity.TokenType;
import com.farmerapp.entity.UnverifiedAdmin;
import com.farmerapp.entity.UnverifiedConsumer;
import com.farmerapp.exception.InvalidPasswordException;
import com.farmerapp.exception.OtpExpiredException;
import com.farmerapp.exception.OtpNotVerifiedException;
import com.farmerapp.exception.UserAlreadyExistsException;
import com.farmerapp.exception.UserNotFoundException;
import com.farmerapp.exception.farmerAlreadyExistsException;
import com.farmerapp.payload.AdminDto;
import com.farmerapp.payload.ConsumerDto;
import com.farmerapp.payload.FarmerDto;
import com.farmerapp.payload.ResetPasswordRequest;
import com.farmerapp.repository.AdminRepository;
import com.farmerapp.repository.ConsumerRepository;
import com.farmerapp.repository.FarmerRepository;
import com.farmerapp.repository.OtpRepository;
import com.farmerapp.repository.UnverifiedAdminRepository;
import com.farmerapp.repository.UnverifiedConsumerRepository;
import com.farmerapp.request.AdminUpdateRequest;
import com.farmerapp.response.ApiResponse;
import com.farmerapp.util.AppUtils;
import com.farmerapp.util.JwtUtil;

@Service
public class AdminService {

	@Autowired
	private AdminRepository adminRepository;
	@Autowired
	private FarmerRepository farmerRepository;
	@Autowired
	private ConsumerRepository consumerRepository;
	@Autowired
	private UnverifiedAdminRepository unverifiedadminRepository;
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
	

	public ApiResponse registerAdmin(AdminDto admin) {
		if (adminRepository.existsByEmail(admin.getEmail())) {
//			return ApiResponse.builder().message("Admin Already Exists").response(admin.getEmail()).build();
	        	throw new UserAlreadyExistsException("Consumer Already Exists");
		}

//	        String additionalData = convertToJson(farmer);

		UnverifiedAdmin unverifiedadmin = UnverifiedAdmin.builder().name(admin.getName()).email(admin.getEmail())
				.password(passwordEncoder.encode(admin.getPassword())).build();

		unverifiedadminRepository.save(unverifiedadmin);

		Map<String, Object> response = new HashMap<String, Object>();
		String token = jwtUtil.generateToken(admin.getEmail(), OtpType.REGISTER, TokenType.AUTH_TOKEN,"ADMIN");
		response.put("token", token);
		response.put("otp", emailService.sendOtp(admin.getEmail(), OtpType.REGISTER));

		return ApiResponse.builder().message("Verify your email").response(response).build();
	}

	public ApiResponse verifyadminOtp(String otp) {

		String token = appUtils.getTokenFromHeader();
//			if(token!=null)
		String email = jwtUtil.getUsername(token);
		Map<String , Object>res = new HashMap<>();

		Otp dbOtp = otpRepository.findByEmail(email);
		if (dbOtp != null) {
			if (TokenType.AUTH_TOKEN.toString().equals((String) jwtUtil.getHeader(token, "tokenType")) && dbOtp.getOtpCode().equals(otp)) {
				if (dbOtp.getExpirationTime().isAfter(LocalDateTime.now()) ) {

					if ("REGISTER".toString().equals(jwtUtil.getHeader(token, "otpType"))) {

						UnverifiedAdmin unverifiedadmin = unverifiedadminRepository.findByEmail(email);

//						 UserDto user = convertToUser(unvu);

						Admin admin = new Admin();
						admin.setName(unverifiedadmin.getName());
						admin.setEmail(unverifiedadmin.getEmail());
						admin.setPassword((unverifiedadmin.getPassword()));

						adminRepository.save(admin);
						unverifiedadminRepository.delete(unverifiedadmin);
						// Clean up temporary storage

						otpRepository.delete(dbOtp);

						String accessToken = jwtUtil.generateToken(email, OtpType.REGISTER, TokenType.ACCESS_TOKEN,"ADMIN");
						res.put("accessToken", accessToken);
						return ApiResponse.builder().message("User successfully verify").response(res).build();

					} else if ("LOGIN".toString().equals(jwtUtil.getHeader(token, "otpType"))) {
						dbOtp.setExpirationTime(LocalDateTime.now());

						otpRepository.delete(dbOtp);
						String accessToken = jwtUtil.generateToken(email, OtpType.LOGIN, TokenType.ACCESS_TOKEN,"ADMIN");
						res.put("accessToken", accessToken);
						return ApiResponse.builder().message("User successfully verify & Login").response(res)
								.build();
					}
//					return ApiResponse.builder().message("Not match OTP_TYPE").build();
					throw new OtpNotVerifiedException("Not match OTP_TYPE");
				}
//				return ApiResponse.builder().message("Otp expired").build();
				throw new OtpExpiredException("Otp expired");
			}
//			return ApiResponse.builder().message("Invalid OTP").build();
			throw new OtpNotVerifiedException("Invalid OTP");
		}
//		return ApiResponse.builder().message("User Not Found ").build();
		throw new UserNotFoundException("User Not Found ");

	}

	public ApiResponse loginAdmin(String email, String password) {

		if (adminRepository.existsByEmail(email)) {

			Admin admin = adminRepository.findByEmail(email)
					.orElseThrow(() -> new UserNotFoundException("User Not Found " + email));

			if (!passwordEncoder.matches(password, admin.getPassword())) {
//				return ApiResponse.builder().message("Invalid Password").build();
	    	    	throw new InvalidPasswordException("Invalid Password");
			}

			Map<String, Object> response = new HashMap<>();
			String token = jwtUtil.generateToken(email, OtpType.LOGIN, TokenType.AUTH_TOKEN,"ADMIN");
//						userRepo.getByEmailAndPassword(email, password);
			response.put("token", token);
			response.put("otp", emailService.sendOtp(email, OtpType.LOGIN));

			return ApiResponse.builder().message("Verify your email").response(response).build();
		} else
//			return ApiResponse.builder().message("User Not Found").response(email).build();
		   throw new UserNotFoundException("User Not Found" + email);
	}

	public ApiResponse forgotPassword(String email) {

		if (adminRepository.existsByEmail(email)) {

			Admin admin = adminRepository.findByEmail(email)
					.orElseThrow(() -> new UserNotFoundException(" not found with email: " + email));

			Map<String, Object> response = new HashMap<String, Object>();
			String token = jwtUtil.generateToken(admin.getEmail(), OtpType.FORGET_PASSWORD, TokenType.AUTH_TOKEN,"ADMIN");
			response.put("token", token);
			response.put("otp",  emailService.sendOtp(admin.getEmail(), OtpType.FORGET_PASSWORD));

			return ApiResponse.builder().message("Reset your password").response(response).build();
		} else
//			return ApiResponse.builder().message("Farmer Not Found").response(email).build();
		    throw new UserNotFoundException("Admin Not Found" + email);
	}

	public ApiResponse resetPassword(ResetPasswordRequest request) {

		String token = appUtils.getTokenFromHeader();
//			if(token!=null)
		String email = jwtUtil.getUsername(token);

		Otp dbOtp = otpRepository.findByEmail(email);
		if (dbOtp != null) {
			if (TokenType.AUTH_TOKEN.toString().equals((String) jwtUtil.getHeader(token, "tokenType")) && dbOtp.getOtpCode().equals(request.getOtp())) {
				if (dbOtp.getExpirationTime().isAfter(LocalDateTime.now())
						) {

					if ("FORGET_PASSWORD".toString().equals(jwtUtil.getHeader(token, "otpType"))) {

						if (adminRepository.existsByEmail(email)) {
							
							Admin admin = adminRepository.findByEmail(email)
									.orElseThrow(() -> new UserNotFoundException("Admin not found"));

							admin.setPassword(passwordEncoder.encode(request.getNewPassword()));
							adminRepository.save(admin);
							
							otpRepository.delete(dbOtp);

							return ApiResponse.builder().message("Password reset  successfully ")
									.response(email).build();
						} else
//							return ApiResponse.builder().message("Farmer Not Found").build();
						throw new UserNotFoundException("Admin Not Found" + email);
					}
//					return ApiResponse.builder().message("Not match OTP_TYPE").build();
				throw new OtpNotVerifiedException("Not match OTP_TYPE");
				}
//				return ApiResponse.builder().message("Otp expired").build();
			throw new OtpExpiredException("Otp expired");
			}
//			return ApiResponse.builder().message("Invalid OTP").build();
		throw new OtpNotVerifiedException("Invalid OTP");
		}
//		return ApiResponse.builder().message("User Not Found ").build();
	throw new UserNotFoundException("User Not Found ");

	}

	
	  public ApiResponse getAdminById(Long adminId) {
	      
		  Admin admin = adminRepository.findById(adminId)
	                .orElseThrow(() -> new UserNotFoundException("Admin Not Found: " + adminId));

	        return ApiResponse.builder()
	                .message("Admin details fetched successfully")
	                .response(admin)
	                .build();
	    }
	  
  public ApiResponse getAdmin() {
	  
	     String token = appUtils.getTokenFromHeader();
//		if(token!=null)
	     String email = jwtUtil.getUsername(token);
		  Admin admin = adminRepository.findByEmail(email)
	                .orElseThrow(() -> new UserNotFoundException("Admin Not Found: " + email));

	        return ApiResponse.builder()
	                .message("Admin details fetched successfully")
	                .response(admin)
	                .build();
	    }

	    public ApiResponse getAllAdmins() {
	        List<Admin> admins = adminRepository.findAll();
	        return ApiResponse.builder()
	                .message("Admins fetched successfully")
	                .response(admins)
	                .build();
	    }

	    public ApiResponse updateAdmin(AdminUpdateRequest updatedAdmin) {
	        
	    	 String token = appUtils.getTokenFromHeader();
//	 		if(token!=null)
	 	     String email = jwtUtil.getUsername(token);
	    	
//	 	    Admin admin = adminRepository.findByEmail(email)
//	                .orElseThrow(() -> new UserNotFoundException("Admin Not Found: " + email));
	 	    
	    	Admin admin = adminRepository.findByEmailAndStatus(email, true)
	                .orElseThrow(() -> new UserNotFoundException("Admin Not Found: " + email));

	        if (updatedAdmin.getName() != null) {
	            admin.setName(updatedAdmin.getName());
	        }

	        adminRepository.save(admin);

	        return ApiResponse.builder()
	                .message("Admin details updated successfully")
	                .response(admin)
	                .build();
	    }

	    public ApiResponse softDeleteAdmin(String email) {
	      
	    	Admin admin = adminRepository.findByEmailAndStatus(email, true)
	                .orElseThrow(() -> new UserNotFoundException("Admin Not Found: " + email));

	        admin.setStatus(false);
	        adminRepository.save(admin);

	        return ApiResponse.builder()
	                .message("Admin account deactivated successfully")
	                .response(email)
	                .build();
	    }

	    public ApiResponse activateAdmin(String email) {
	       
	    	Admin admin = adminRepository.findByEmail(email)
	                .orElseThrow(() -> new UserNotFoundException("Admin Not Found: " + email));

	        if (admin.isStatus()) {
	            return ApiResponse.builder()
	                    .message("Account already activated")
	                    .response(email)
	                    .build();
	        }

	        admin.setStatus(true);
	        adminRepository.save(admin);

	        return ApiResponse.builder()
	                .message("Account activated successfully")
	                .response(email)
	                .build();
	    }
	    
	    
	    public ApiResponse updateFarmerStatus(Long  id, boolean status) {
	       
	    	 int updatedCount = farmerRepository.updateStatusById(id, status);
	    	    if (updatedCount == 0) {
	    	        throw new UserNotFoundException("Farmer not found with id: " + id);
	    	    }
	    	
	    	    return ApiResponse.builder()
	    	            .message("Account " + (status ? "activated" : "deactivated") + " successfully")
	    	            .response("Farmer ID: " + id)
	    	            .build();
	    }
	    
	  
	    public ApiResponse updateConsumerStatus(Long id,boolean status) {
	    	
	    	System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
	    	
	    	  int updated = consumerRepository.updateStatusById(id, status);
	    	    if (updated == 0) {
	    	        throw new UserNotFoundException("Consumer not found with ID: " + id);
	    	    }

	    	    return ApiResponse.builder()
	    	            .message("Consumer account " + (status ? "activated" : "deactivated") + " successfully")
	    	            .response("Consumer ID: " + id)
	    	            .build();
	    }
	
	

}
