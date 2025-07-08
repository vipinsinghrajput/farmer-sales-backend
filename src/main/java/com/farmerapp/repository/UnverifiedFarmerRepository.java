package com.farmerapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farmerapp.entity.UnverifiedFarmer;
@Repository
public interface UnverifiedFarmerRepository extends JpaRepository<UnverifiedFarmer, String> {

	UnverifiedFarmer findByEmail(String email);

}
