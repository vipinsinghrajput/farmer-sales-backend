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

import java.util.Collections;
import java.io.IOException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import com.farmerapp.util.JwtUtil;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
    private JwtUtil jwtUtil;

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
		if (tokenType != null && tokenType.equals("ACCESS_TOKEN")) {
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    // Extract role from token to avoid database hit
                    String role = jwtUtil.getRole(token);
                    if (role != null) {
                        UserDetails userDetails = new User(
                                username,
                                "",
                                Collections.singletonList(new SimpleGrantedAuthority(role))
                        );

                        if (!jwtUtil.extractClaim(token, io.jsonwebtoken.Claims::getExpiration).before(new java.util.Date())) {
                            UsernamePasswordAuthenticationToken authToken =
                                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
		} 
        
        filterChain.doFilter(request, response);
    }
}
