package com.farmerapp.response;

import com.farmerapp.entity.DeliveryPerson.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder 
public class DeliveryPersonResponse {
	
	
	    private Long id;
	    private String name;
	    private String mobile;
	    private String email;
	    private String vehicleNumber;
	    private String licenseNumber;
	    private Status status;
	    private boolean active ;
	}

