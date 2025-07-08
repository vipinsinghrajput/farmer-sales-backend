package com.farmerapp.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.farmerapp.entity.DeliveryPerson;
import com.farmerapp.entity.Farmer;
import com.farmerapp.entity.OtpType;
import com.farmerapp.entity.TokenType;
import com.farmerapp.entity.UnverifiedDeliveryPerson;
import com.farmerapp.entity.UnverifiedFarmer;
import com.farmerapp.entity.DeliveryPerson.Status;
import com.farmerapp.entity.Otp;
import com.farmerapp.exception.AccountDeactivatedException;
import com.farmerapp.exception.InvalidPasswordException;
import com.farmerapp.exception.OtpExpiredException;
import com.farmerapp.exception.OtpNotVerifiedException;
import com.farmerapp.exception.UserAlreadyExistsException;
import com.farmerapp.exception.UserNotFoundException;
import com.farmerapp.payload.DeliveryPersonRegisterDTO;
import com.farmerapp.payload.FarmerDto;
import com.farmerapp.payload.ResetPasswordRequest;
import com.farmerapp.repository.DeliveryPersonRepository;
import com.farmerapp.repository.OtpRepository;
import com.farmerapp.repository.UnverifiedDeliveryPersonRepository;
import com.farmerapp.request.DeliveryPersonDTO;
import com.farmerapp.request.OtpVerifyRequest;
import com.farmerapp.request.farmerUpdateRequest;
import com.farmerapp.response.ApiResponse;
import com.farmerapp.response.DeliveryPersonResponse;
import com.farmerapp.util.AppUtils;
import com.farmerapp.util.JwtUtil;


@Service
public class DeliveryPersonService {

    @Autowired
    private DeliveryPersonRepository deliveryPersonRepository;
    
    @Autowired
    private UnverifiedDeliveryPersonRepository unverifieddeliveryPersonRepository;
    
    @Autowired
    private EmailService emailService;
    @Autowired 
    private OtpRepository otpRepository;
    @Autowired 
    private PasswordEncoder passwordEncoder;
    @Autowired 
    private JwtUtil jwtUtil;
    
    @Autowired
    private AppUtils appUtils;
    

    public ApiResponse register(DeliveryPersonRegisterDTO dto) {
        if (deliveryPersonRepository.existsByEmail(dto.getEmail())) {
//            return ApiResponse.builder().message("Farmer Already Exists").response(farmer.getEmail()).build();
                    	throw new UserAlreadyExistsException("User Already Exists");
        }

        if(deliveryPersonRepository.existsBymobile(dto.getMobile())) {
        	throw new UserAlreadyExistsException("Duplicate entry : " + dto.getMobile());
        }
//        String additionalData = convertToJson(farmer);
        
        UnverifiedDeliveryPerson dp = UnverifiedDeliveryPerson.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .mobile(dto.getMobile())
                .password(passwordEncoder.encode(dto.getPassword())) // Note: Hash before saving in real-world apps
                .vehicleNumber(dto.getVehicleNumber())
                .licenseNumber(dto.getLicenseNumber())
                .status(DeliveryPerson.Status.AVAILABLE)
                .build();
       
        unverifieddeliveryPersonRepository.save(dp);

        Map <String, Object> response = new HashMap<String, Object>();
        String token = jwtUtil.generateToken(dto.getEmail(), OtpType.REGISTER, TokenType.AUTH_TOKEN,"DELIVERY_BOY");
        response.put("token", token);
        response.put("otp", emailService.sendOtp(dto.getEmail(), OtpType.REGISTER));

        return ApiResponse.builder().message("Verify your email").response(response).build();
    }

    
    
    public ApiResponse verifyOtp(String otp) {
        
    	String token = appUtils.getTokenFromHeader();
    	System.err.println("Token =====  " +token);
    	Map< String , Object>res = new HashMap<>();
//		if(token!=null)
	String email = jwtUtil.getUsername(token);

	Otp dbOtp = otpRepository.findByEmail(email);
	if (dbOtp != null ) {
		if (TokenType.AUTH_TOKEN.toString().equals((String) jwtUtil.getHeader(token, "tokenType"))  && dbOtp.getOtpCode().equals(otp)) {
			if (dbOtp.getExpirationTime().isAfter(LocalDateTime.now()) ) {
				
				if ("REGISTER".toString().equals(jwtUtil.getHeader(token, "otpType"))) {
					
					 UnverifiedDeliveryPerson temp = unverifieddeliveryPersonRepository.findByEmail(email);
			           

//					 Farmer farmer = new Farmer();
//				        farmer.setName(unverifiedfarmer.getName());  
//				        farmer.setEmail(unverifiedfarmer.getEmail());
//				        farmer.setMobileNumber(unverifiedfarmer.getMobileNumber());
//				        farmer.setPassword(unverifiedfarmer.getPassword());
//				        farmer.setFarmName(unverifiedfarmer.getFarmName());
//				        farmer.setFarmAddress(unverifiedfarmer.getFarmAddress());
//				        farmer.setPincode(unverifiedfarmer.getPincode());
//				        farmer.setFarmDescription(unverifiedfarmer.getFarmDescription());
//				        farmer.setFarmLicenseNumber(unverifiedfarmer.getFarmLicenseNumber());

				        DeliveryPerson dp = new DeliveryPerson();
				       dp.setName(temp.getName());
				       dp.setEmail(temp.getEmail());
				       dp.setMobile(temp.getMobile());
				       dp.setPassword(temp.getPassword());
				       dp.setLicenseNumber(temp.getLicenseNumber());
				       dp.setVehicleNumber(temp.getVehicleNumber());
				       dp.setStatus(DeliveryPerson.Status.AVAILABLE);
				      
				               

				        deliveryPersonRepository.save(dp);
				        otpRepository.delete(dbOtp);
				        unverifieddeliveryPersonRepository.delete(temp);
				     
					String accessToken = jwtUtil.generateToken(email, OtpType.REGISTER, TokenType.ACCESS_TOKEN,"DELIVERY_BOY");
					
					res.put("accessToken", accessToken);
					return  ApiResponse.builder().message("User successfully verify").response(res).build();
					
				} else if ("LOGIN".toString().equals(jwtUtil.getHeader(token, "otpType"))) {
					dbOtp.setExpirationTime(LocalDateTime.now());          
					otpRepository.delete(dbOtp);
					String accessToken = jwtUtil.generateToken(email, OtpType.LOGIN, TokenType.ACCESS_TOKEN,"DELIVERY_BOY");
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
    
    
    
    
//    @Transactional
//    public ApiResponse verifyOtp(OtpVerifyRequest otp) {
//       
//    	Otp otpEntity = otpRepository.findByEmailAndType(otp.getEmail(), OtpType.REGISTER)
//            .orElseThrow(() -> new OtpNotVerifiedException("OTP not found"));
//
//        if (!otpEntity.getOtpCode().equals(otp.getOtp())) {
//            throw new OtpNotVerifiedException("Invalid OTP");
//        }
//
//        if (otpEntity.getExpirationTime().isBefore(LocalDateTime.now())) {
//            throw new OtpExpiredException("OTP expired");
//        }
//
//        UnverifiedDeliveryPerson temp = unverifieddeliveryPersonRepository.findByEmail(otp.getEmail());
//           
//
//        DeliveryPerson dp = DeliveryPerson.builder()
//                .name(temp.getName())
//                .email(temp.getEmail())
//                .mobile(temp.getMobile())
//                .password(temp.getPassword()) // Should be hashed!
//                .vehicleNumber(temp.getVehicleNumber())
//                .licenseNumber(temp.getLicenseNumber())
//                .status(DeliveryPerson.Status.AVAILABLE)
//                .build();
//
//        deliveryPersonRepository.save(dp);
//        otpRepository.delete(otpEntity);
//        unverifieddeliveryPersonRepository.delete(temp);
//
//        return  ApiResponse.builder().message("Registration verified and completed!").build();
//    }

  
    public ApiResponse login(String email, String password) {

    	if (deliveryPersonRepository.existsByEmail(email)) {
    	
    		  DeliveryPerson dp = deliveryPersonRepository.findByEmail(email)
    		            .orElseThrow(() -> new UserNotFoundException("Not Found" + email));
    	  
    	   
    	   if (!dp.isActive()) {
   	        throw new AccountDeactivatedException("Account is deactivated. Please contact support.");
   	    }
    	    if (!passwordEncoder.matches(password, dp.getPassword())) {
//    	       return ApiResponse.builder().message("Invalid Password").build();
    	    	throw new InvalidPasswordException("Invalid Password");
    	    }
    	
				Map<String, Object> response = new HashMap<>();
				String token = jwtUtil.generateToken(email, OtpType.LOGIN, TokenType.AUTH_TOKEN,"DELIVERY_BOY");
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
    
//    public ApiResponse login(String email, String password) {
//        DeliveryPerson dp = deliveryPersonRepository.findByEmail(email)
//            .orElseThrow(() -> new UserNotFoundException("Not Found"));
//       
//        if(!passwordEncoder.matches(password, dp.getPassword())) {
//            throw new InvalidPasswordException("Invalid credentials");
//        }
//        return ApiResponse.builder().message("User successfully Login")
//				.build();
//    }
    
   
    public ApiResponse forgotPassword(String email) {
        
    	if (deliveryPersonRepository.existsByEmail(email)) {
    		
    	
    	DeliveryPerson dp = deliveryPersonRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user not found with email: " + email));

    	 if (!dp.isActive()) {
    	        throw new AccountDeactivatedException("Account is deactivated. Please contact support.");
    	    }
    	
        Map <String, Object> response = new HashMap<String, Object>();
        String token = jwtUtil.generateToken(dp.getEmail(), OtpType.FORGET_PASSWORD, TokenType.AUTH_TOKEN,"DELIVERY_BOY");
        response.put("token", token);
        response.put("otp", emailService.sendOtp(dp.getEmail(), OtpType.REGISTER));

        return ApiResponse.builder().message("Reset your password").response(response).build();
    }else
//    	return ApiResponse.builder().message("Farmer Not Found").response(email).build();
    	throw new UserNotFoundException("user Not Found" + email);
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

					if(deliveryPersonRepository.existsByEmail(email)) {
					
					
                DeliveryPerson dp = deliveryPersonRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user not found"));
                
                if (!dp.isActive()) {
           	        throw new AccountDeactivatedException("Account is deactivated. Please contact support.");
           	    }

        dp.setPassword(passwordEncoder.encode(request.getNewPassword()));
        deliveryPersonRepository.save(dp);
        otpRepository.delete(dbOtp);
        
        return ApiResponse.builder().message("Password reset  successfully ").response(email).build();
				}else
//					return	ApiResponse.builder().message("Farmer Not Found").build();
					throw new UserNotFoundException("user Not Found");
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
    
    

    
    public ApiResponse getById(Long Id) {
        
    	DeliveryPerson dp = deliveryPersonRepository.findById(Id)
                .orElseThrow(() -> new UserNotFoundException("User Not Found: " + Id));

        return ApiResponse.builder()
                .message("Delivery_boy  details fetched successfully")
                .response(mapToResponse(dp))
                .build();
    }

 public ApiResponse getDelivery_boy() {
	 
	    String token = appUtils.getTokenFromHeader();
	    String email = jwtUtil.getUsername(token);
	    
	
    	DeliveryPerson dp = deliveryPersonRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User Not Found: "));

        return ApiResponse.builder()
                .message("Delivery_boy details fetched successfully")
                .response(mapToResponse(dp))
                .build();
    }

   
    
    public ApiResponse update(DeliveryPersonDTO dto) {
       
    	String token = appUtils.getTokenFromHeader();
//		if(token!=null)
	    String email = jwtUtil.getUsername(token);
    	
	    DeliveryPerson dp = deliveryPersonRepository.findByEmailAndActive(email, true)
                .orElseThrow(() -> new UserNotFoundException("User Not Found: " + email));

//    	 if(farmerRepository.existsBymobileNumber(updatedFarmer.getMobileNumber())) {
//         	throw new UserAlreadyExistsException("Duplicate entry : " + updatedFarmer.getMobileNumber() );
//         }
//    	
    	 if (dto.getName() != null) {
    	        dp.setName(dto.getName());
    	    }
    	 if(dto.getLicenseNumber() != null) {
    		 dp.setLicenseNumber(dto.getLicenseNumber());
    	 }
    	 if(dto.getVehicleNumber() != null) {
    		 dp.setVehicleNumber(dto.getVehicleNumber());
    	 }
       
    	 deliveryPersonRepository.save(dp);

          return ApiResponse.builder()
                  .message("User details updated successfully")
                  .response(dp)
                  .build();
          
    }

    
    public ApiResponse softDelete(String email) {
    	 DeliveryPerson dp = deliveryPersonRepository.findByEmailAndActive(email, true)
                 .orElseThrow(() -> new UserNotFoundException("User Not Found: " + email));

         dp.setActive(false);
         deliveryPersonRepository.save(dp);

         return ApiResponse.builder()
                 .message("User account deactivated successfully")
                 .response(email)
                 .build();
     }
    
    
    public ApiResponse activate(String email) {
        DeliveryPerson dp = deliveryPersonRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User Not Found: " + email));

        if(dp.isActive())
        	return ApiResponse.builder().message("Account allready activated ").response(email).build();
       
        dp.setActive(true);
        deliveryPersonRepository.save(dp);

        return ApiResponse.builder().message("Account activated successfully").response(email).build();
    }
    
     
    
    public ApiResponse getAvailablePersons() {
    	List<DeliveryPerson> dp=   deliveryPersonRepository.findByStatus(Status.AVAILABLE);
    	
    	 List<DeliveryPersonResponse> responseList = dp.stream().map(this::mapToResponse).collect(Collectors.toList());

         return ApiResponse.builder()
                 .message("Fetched successfully")
                 .response(responseList)
                 .build();
    }
    
    public ApiResponse getAllPersons() {
    	List<DeliveryPerson> dp=   deliveryPersonRepository.findAll();;
    	
    	 List<DeliveryPersonResponse> responseList = dp.stream().map(this::mapToResponse).collect(Collectors.toList());

         return ApiResponse.builder()
                 .message("Fetched  successfully")
                 .response(responseList)
                 .build();
    }

    public ApiResponse updateStatus(Long id, Status status) {
      
    	DeliveryPerson dp = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(" User Not found"));
        dp.setStatus(status);
       deliveryPersonRepository.save(dp);
       return ApiResponse.builder()
               .message("Updated successfully")
               .response(mapToResponse(dp))
               .build();
    }
    
    
    private DeliveryPersonResponse mapToResponse(DeliveryPerson dp) {
        return DeliveryPersonResponse.builder()
                .id(dp.getId())
                .name(dp.getName())
                .email(dp.getEmail())
                .mobile(dp.getMobile())
                .vehicleNumber(dp.getVehicleNumber())
                .licenseNumber(dp.getLicenseNumber())
                .status(dp.getStatus())
                .active(dp.isActive())
                .build();
    }

}
