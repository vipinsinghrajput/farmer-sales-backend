package com.farmerapp.security;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.farmerapp.entity.Admin;
import com.farmerapp.entity.Consumer;
import com.farmerapp.entity.DeliveryPerson;
import com.farmerapp.entity.Farmer;
import com.farmerapp.repository.AdminRepository;
import com.farmerapp.repository.ConsumerRepository;
import com.farmerapp.repository.DeliveryPersonRepository;
import com.farmerapp.repository.FarmerRepository;



@Service
public class CustomUserDetailsService implements UserDetailsService {

//	@Autowired
//	private UserRepository userRepository;
//
//  
//
//    
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        User user = ( userRepository.findByEmail(email))
//        		.orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        return new org.springframework.security.core.userdetails.User(
//                user.getEmail(),
//                user.getPassword(),
//                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())));
//    }
	    @Autowired
	    private  FarmerRepository farmerRepository;
	    @Autowired
	    private  ConsumerRepository consumerRepository;
        @Autowired
	    private  AdminRepository adminRepository;
	    @Autowired
	    private  DeliveryPersonRepository deliveryPersonRepository;
//
//	    @Override
//	    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//	        Farmer farmer = farmerRepository.findByEmail(email).orElse(null);
//	        Consumer consumer = consumerRepository.findByEmail(email).orElse(null);
//
//	        if (farmer != null) {
//	            return new User(
//	                farmer.getEmail(),
//	                farmer.getPassword(),
//	                Collections.singletonList(new SimpleGrantedAuthority("FARMER"))
//	            );
//	        } else if (consumer != null) {
//	            return new User(
//	                consumer.getEmail(),
//	                consumer.getPassword(),
//	                Collections.singletonList(new SimpleGrantedAuthority("CONSUMER"))
//	            );
//	        } else {
//	            throw new UsernameNotFoundException("User not found with email: " + email);
//	        }
//	    }
	
	 @Override
	    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	        Farmer farmer = farmerRepository.findByEmail(email).orElse(null);
	        Consumer consumer = consumerRepository.findByEmail(email).orElse(null);
            Admin admin = adminRepository.findByEmail(email).orElse(null);
            DeliveryPerson deliveryPerson= deliveryPersonRepository.findByEmail(email).orElse(null);
	        
	        if (farmer == null && consumer == null && admin == null && deliveryPerson == null) {
	            throw new UsernameNotFoundException("User not found with email: " + email);
	        }

	        Set<SimpleGrantedAuthority> authorities = new HashSet<SimpleGrantedAuthority>();

	        String password = null;

	        if (farmer != null) {
	            authorities.add(new SimpleGrantedAuthority("FARMER"));
	            password = farmer.getPassword(); // Farmer password (assuming both have the same password)
	        }

	        if (consumer != null) {
	            authorities.add(new SimpleGrantedAuthority("CONSUMER"));
	            if (password == null) {
	                password = consumer.getPassword(); // If user is only a consumer
	            }
	        }
	        
	        if (admin != null) {
	            authorities.add(new SimpleGrantedAuthority("ADMIN"));
	            if (password == null) {
	                password = admin.getPassword(); // If user is only a consumer
	            }
	        }
	        
	        if (deliveryPerson != null) {
	            authorities.add(new SimpleGrantedAuthority("DELIVERY_BOY"));
	            if (password == null) {
	                password = deliveryPerson.getPassword(); // If user is only a consumer
	            }
	        }

	        return new User(email, password, authorities);
   
}
	 }
