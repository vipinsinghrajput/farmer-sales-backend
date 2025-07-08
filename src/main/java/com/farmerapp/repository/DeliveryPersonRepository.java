package com.farmerapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.farmerapp.entity.DeliveryPerson;
import com.farmerapp.entity.DeliveryPerson.Status;
import com.farmerapp.entity.OrderEntity;

public interface DeliveryPersonRepository extends JpaRepository<DeliveryPerson, Long>{

	    Optional<DeliveryPerson> findByEmail(String email);

		List<DeliveryPerson> findByStatus(Status status);

		boolean existsByEmail(String email);

		boolean existsBymobile(String mobile);

		Optional<DeliveryPerson> findByEmailAndActive(String email, boolean b);

		Optional<DeliveryPerson> findByIdAndStatus(Long deliveryPersonId, Status status); 
	}
