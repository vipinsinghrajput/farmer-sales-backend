package com.farmerapp.repository;


import org.springframework.data.jpa.repository.JpaRepository;


import com.farmerapp.entity.UnverifiedDeliveryPerson;


public interface UnverifiedDeliveryPersonRepository extends JpaRepository<UnverifiedDeliveryPerson, String>{

	UnverifiedDeliveryPerson findByEmail(String email);

}
