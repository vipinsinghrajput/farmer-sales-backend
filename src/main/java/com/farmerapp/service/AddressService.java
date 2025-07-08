package com.farmerapp.service;

import com.farmerapp.exception.OrderNotFoundException;
import com.farmerapp.exception.UserNotFoundException;
import com.farmerapp.entity.Address;
import com.farmerapp.entity.Consumer;
import com.farmerapp.entity.OrderEntity;
import com.farmerapp.entity.OrderStatus;
import com.farmerapp.entity.UnverifiedFarmer;
import com.farmerapp.repository.AddressRepository;
import com.farmerapp.repository.ConsumerRepository;
import com.farmerapp.repository.OrderRepository;
import com.farmerapp.response.AddressResponse;
import com.farmerapp.response.ApiResponse;
import com.farmerapp.service.AddressService;
import com.farmerapp.util.AppUtils;
import com.farmerapp.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService{

	@Autowired
    private AddressRepository addressRepository;
	@Autowired
    private ConsumerRepository consumerRepository;
    @Autowired
	private OrderRepository orderRepository;
	@Autowired 
	private  JwtUtil util;
	@Autowired
	private AppUtils utils;
	@Autowired
    private GeocodingService geocodingService;

	 private String formatDeliveryAddress(Address userRequest) {
			return String.format("%s, %s, %s, %s, %s", userRequest.getStreet().trim(), userRequest.getCity().trim(),
					userRequest.getState().trim(), userRequest.getPostalCode().trim(), userRequest.getCountry().trim());
		}
	 
	public ApiResponse saveAddress(Address address) {
		
		String token = utils.getTokenFromHeader();
        String email = util.getUsername(token);

       Consumer consumer = consumerRepository.findByEmail(email)
               .orElseThrow(() -> new UserNotFoundException("Consumer not found"));
       
//    	Consumer consumer = consumerRepository.findById(consumerId)
//                .orElseThrow(() -> new UserNotFoundException("Consumer not found"));

        address.setConsumer(consumer);
       
        double[] latLon = geocodingService.fetchCoordinatesWithFallback(formatDeliveryAddress(address),
        		address.getCity(),address.getState(),address.getCountry(), address.getPostalCode());
		address.setLatitude(latLon[0]);
		address.setLongitude(latLon[1]);
        
        Address savedAddress = addressRepository.save(address);

        return ApiResponse.builder()
                .message("Address saved successfully")
                .response(mapToResponse(savedAddress))
                .build();
    }

    
    public ApiResponse getAllAddresses() {
    	
    	String token = utils.getTokenFromHeader();
        String email = util.getUsername(token);

       Consumer consumer = consumerRepository.findByEmail(email)
               .orElseThrow(() -> new UserNotFoundException("Consumer not found"));
       
//    	Consumer consumer = consumerRepository.findById(consumerId)
//                .orElseThrow(() -> new UserNotFoundException("Consumer not found"));

        List<Address> addresses = addressRepository.findByConsumerId(consumer.getId());
        List<AddressResponse> addressesList  = addresses.stream().map(this::mapToResponse).collect(Collectors.toList());

        return ApiResponse.builder() 
                .message("Address list fetched successfully")
                .response(addressesList)
                .build();
    }
    
    
    public ApiResponse getAddressById(Long addressId) {
//        Consumer consumer = consumerRepository.findById(consumerId)
//                .orElseThrow(() -> new UserNotFoundException("Consumer not found"));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new UserNotFoundException("Address not found or not owned by this consumer"));

        return ApiResponse.builder()
                .message("Address fetched successfully")
                .response(mapToResponse(address))
                .build();
        
    }
    
    
    public ApiResponse updateAddress(Long addressId, Address updatedAddress) {
        
    	String token = utils.getTokenFromHeader();
      String email = util.getUsername(token);

     Consumer consumer = consumerRepository.findByEmail(email)
             .orElseThrow(() -> new UserNotFoundException("Consumer not found"));
     
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new UserNotFoundException("Address not found or not owned by this consumer"));

        boolean hasActiveOrder = orderRepository.existsByDeliveryAddressAndStatusNotIn(
                address, List.of(OrderStatus.DELIVERED, OrderStatus.CANCELED));

        if (hasActiveOrder) {
            throw new OrderNotFoundException("Cannot update address used in active or incomplete orders.");
        }
        
        address.setStreet(updatedAddress.getStreet());
        address.setCity(updatedAddress.getCity());
        address.setState(updatedAddress.getState());
        address.setPostalCode(updatedAddress.getPostalCode());
        
        double[] latLon = geocodingService.fetchCoordinatesWithFallback(address.getStreet(),
        		address.getCity(),address.getState(),address.getCountry(), address.getPostalCode());
		address.setLatitude(latLon[0]);
		address.setLongitude(latLon[1]);

        Address savedAddress = addressRepository.save(address);

        return ApiResponse.builder()
                .message("Address updated successfully")
                .response(mapToResponse(savedAddress))
                .build();
    }
    
    
    public ApiResponse deleteAddress(Long addressId) {
      
//    	Consumer consumer = consumerRepository.findByconsu(consumerId)
//                .orElseThrow(() -> new UserNotFoundException("Consumer not found"));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new OrderNotFoundException("Address not found "));

       
        boolean hasActiveOrder = orderRepository.existsByDeliveryAddressAndStatusNotIn(
                address, List.of(OrderStatus.DELIVERED, OrderStatus.CANCELED));

        if (hasActiveOrder) {
            throw new OrderNotFoundException("Cannot update address used in active or incomplete orders.");
        }
        
        addressRepository.delete(address);

        return ApiResponse.builder()
                .message("Address deleted successfully")
                .response(null)
                .build();
    }
 
    public AddressResponse mapToResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .Country(address.getCountry())
                .postalCode(address.getPostalCode())
                .build();
    }
}
