package com.farmerapp.entity;

import com.farmerapp.entity.DeliveryPerson.Status;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnverifiedDeliveryPerson {

	    private String name;
	    @Id
	    private String email;
	    private String mobile;
	    private String password;
	   
	    private String vehicleNumber;
	    
	    private String licenseNumber;
	    private Status status;

	   
}
