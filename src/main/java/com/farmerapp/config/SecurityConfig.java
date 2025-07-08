package com.farmerapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.farmerapp.handler.CustomAccessDeniedHandler;
import com.farmerapp.handler.CustomAuthenticationEntryPoint;
import com.farmerapp.security.CustomUserDetailsService;
import com.farmerapp.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
	 private CustomUserDetailsService userDetailsService;
	@Autowired
	private JwtAuthenticationFilter authenticationFilter;
	
	
	
	 @Bean
	    public AccessDeniedHandler accessDeniedHandler() {
	        return new CustomAccessDeniedHandler(); 
	    }

	    @Bean
	    public AuthenticationEntryPoint authenticationEntryPoint() {
	        return new CustomAuthenticationEntryPoint();
	    }
	
//	    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//	 @Autowired
//	 private JwtAuthenticationFilter authenticationFilter;
//	    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
//	        this.userDetailsService = userDetailsService;
//	        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//	    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	System.out.println("hellovipin");
        http.csrf(csrf -> csrf.disable()).cors(Customizer.withDefaults())
                .authorizeHttpRequests(requests ->  requests
               
//                		 .requestMatchers(
//                	                "/admin/**",
//                	                "/consumer/getall", "/consumer/getbyid", "/consumer/activate",
//                	                "/farmer/getall", "/farmer/getbyid", "/farmer/activate"
//                	            ).hasAuthority("ADMIN")		
                		.requestMatchers("/farmer/getall").hasAnyAuthority("CONSUMER","ADMIN")
                		.requestMatchers("/products/getall","/products/getbyfarmerid","/products/getallfarmerproducts","/farmer/getbyid","/products/getbyid","/farmer/getall").hasAuthority("CONSUMER")
                		.requestMatchers("/consumer/getbyid").hasAuthority("FARMER")
                		.requestMatchers("/orders/updatestatus").hasAnyAuthority("DELIVERY_BOY","FARMER","ADMIN")
                		.requestMatchers("/consumer/getall", "/consumer/getbyid", "/consumer/activate","/farmer/getall", "/farmer/getbyid", "/farmer/activate","/admin/getall","/admin/getbyid", "/admin/activate","/admin/updatefarmerstatus","/admin/updateconsumerstatus").hasAuthority("ADMIN")
                .requestMatchers("/consumer/**","/farmer/**","/notifications/**","/admin/**","/delivery/**").permitAll()
                .requestMatchers("/products/**","/orders/getfarmerorders","/orders/updatestatus","/consumer/getbyid","/address/getbyaddressid","/orders/assign-delivery").hasAnyAuthority("FARMER","ADMIN")
                .requestMatchers("/orders/**").hasAnyAuthority("CONSUMER","ADMIN")
                .requestMatchers("/reviews/**","/address/**","/cart/**").hasAnyAuthority("CONSUMER","ADMIN")
                
                
                
                
                .anyRequest().authenticated())
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                .accessDeniedHandler(accessDeniedHandler()) 
                .authenticationEntryPoint(authenticationEntryPoint()) 
            );

        return http.build();
    }
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    
    
    @Configuration
    public class WebConfig implements WebMvcConfigurer {

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
        }
    }

}

