package com.farmerapp.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {


	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @ManyToOne
	    @JoinColumn(name = "consumer_id", nullable = false)
	    
	    private Consumer consumer;

	    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	    private List<OrderItem> orderItems;

//	    @Embedded
	    
	    @ManyToOne
	    @JoinColumn(name = "address_id",nullable = false)
	    private Address deliveryAddress; // New Field

	   
	    @NotNull(message = "Status is required")
	    private OrderStatus status;
	    
	    private LocalDateTime orderDate;
	    private Double totalAmount;
	
	    
	    private PaymentStatus paymentStatus;

	   
//	    private DeliveryStatus deliveryStatus = DeliveryStatus.NOT_ASSIGNED;

	    @ManyToOne
	    @JoinColumn(name = "delivery_person_id")
	    private DeliveryPerson deliveryPerson;
	    
	    @Column(name = "delivery_fee")
	    private Double deliveryFee;

	  

}

