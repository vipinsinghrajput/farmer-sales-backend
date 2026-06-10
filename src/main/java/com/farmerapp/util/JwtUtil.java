package com.farmerapp.util;

import org.springframework.stereotype.Component;

import com.farmerapp.entity.OtpType;
import com.farmerapp.entity.TokenType;
import com.farmerapp.exception.ExpiredTokenException;
import com.farmerapp.response.ApiResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;



@Component
public class JwtUtil {

		@Value("${app.jwt.secret}")
		private String secret;

//		Map<String, Object> claim = new HashMap<>();
//		claim.put("tokenType", "Auth_Token");
		public String getUsername(String token) {
			return getClaims(token).getSubject();
		}
		
		public String getRole(String token) {
		    return getClaims(token).getIssuer();
		}

		public String generateToken(String subject, OtpType otpType, TokenType tokenType, String role) {
			Map<String, Object> m = new HashMap<String, Object>();
			if(tokenType.equals(TokenType.AUTH_TOKEN))
				m.put("otpType", otpType);
			m.put("tokenType", tokenType);
			return generateToken(m, subject, tokenType,role);
		}

		private Claims getClaims(String token) {
			try {
				return Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token).getBody();
			} catch (ExpiredJwtException e) {
				throw new ExpiredTokenException("Session expired. Please login again.");
			} catch (SignatureException | MalformedJwtException | IllegalArgumentException e) {
				throw new ExpiredTokenException("Invalid session token. Please login again.");
			}
		} 

		private String generateToken(Map<String, Object> claims, String subject, TokenType tokenType, String role) {
			return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
					.setExpiration(tokenType.equals(TokenType.AUTH_TOKEN)
							? new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10))
							: new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24)))
					.setIssuer(role).signWith(SignatureAlgorithm.HS256, secret.getBytes()).compact();
		}

		public Object getHeader(String token, String key) {
			System.err.println(token);
			return this.getClaims(token).get(key);
		}
		 
		public boolean validateToken(String token, UserDetails userDetails) {
		        final String username = getUsername(token);
		        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
		    }
		 
		private boolean isTokenExpired(String token) {
		        return extractClaim(token, Claims::getExpiration).before(new Date());
		    }  
		 public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		        final Claims claims = extractAllClaims(token);
		        return claimsResolver.apply(claims);
		    }
		 private Claims extractAllClaims(String token) {
		        return Jwts.parser()
		                .setSigningKey(secret.getBytes())
		                .parseClaimsJws(token)
		                .getBody();
		    }
}
