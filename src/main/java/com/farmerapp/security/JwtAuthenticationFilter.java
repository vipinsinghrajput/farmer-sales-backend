package com.farmerapp.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.farmerapp.entity.Consumer;
import com.farmerapp.entity.Farmer;
import com.farmerapp.repository.AdminRepository;
import com.farmerapp.repository.ConsumerRepository;
import com.farmerapp.repository.DeliveryPersonRepository;
import com.farmerapp.repository.FarmerRepository;
import com.farmerapp.util.JwtUtil;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
    private JwtUtil jwtUtil;
	@Autowired
    private CustomUserDetailsService userDetailsService;
	@Autowired
    private  FarmerRepository farmerRepository;
    @Autowired
    private  ConsumerRepository consumerRepository;
    @Autowired
    private  DeliveryPersonRepository deliveryPersonRepository;
	

//    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
//        this.jwtUtil = jwtUtil;
//        this.userDetailsService = userDetailsService;
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        System.out.println(authHeader+"< ======= >authHeader");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String username = jwtUtil.getUsername(token);
        System.out.println("===============  userdetails  ==========  >" +username);
        
        String tokenType = (String) jwtUtil.getHeader(token, "tokenType");
		if (tokenType.equals("ACCESS_TOKEN")) {
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            System.out.println("===============  userdetails  ==========  >" +userDetails);
           try {
        	   if (jwtUtil.validateToken(token, userDetails)) {
        		   
        		   // 🔒 DEACTIVATED USER CHECK
                   boolean isDeactivated = isUserDeactivated(username,userDetails);
                   if (isDeactivated) {
                       response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                       response.getWriter().write("Your account is deactivated by admin.");
                       return;
                   }
        		   
    		   
                   UsernamePasswordAuthenticationToken authToken =
                           new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                   SecurityContextHolder.getContext().setAuthentication(authToken);
               }
           }catch(Exception e) {
        	   e.printStackTrace();
           }
        }

		} 
        
        filterChain.doFilter(request, response);
    }
    
    private boolean isUserDeactivated(String email, UserDetails userDetails) {
        if (userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("FARMER"))) {
            Farmer farmer = farmerRepository.findByEmail(email).orElse(null);
            return farmer != null && !farmer.isStatus();
        }

        if (userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("CONSUMER"))) {
            Consumer consumer = consumerRepository.findByEmail(email).orElse(null);
            return consumer != null && !consumer.isStatus();
        }


        return false; // For admin or other roles, assume active
    }

    
}
