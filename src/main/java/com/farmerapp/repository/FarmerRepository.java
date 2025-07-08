package com.farmerapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.farmerapp.entity.Farmer;

import jakarta.transaction.Transactional;
@Repository
public interface FarmerRepository extends JpaRepository<Farmer, Long>,JpaSpecificationExecutor<Farmer>{

	boolean existsByEmail(String email);

      Optional<Farmer> findByEmail(String email);

	boolean existsByEmailAndPassword(String email, String password);

	Optional<Farmer> findByEmailAndStatus(String email, boolean b);

	boolean existsBymobileNumber(String mobileNumber);

	@Modifying
	@Transactional
	@Query("UPDATE Farmer f SET f.status = :status WHERE f.id = :id")
	int updateStatusById(@Param("id") Long id, @Param("status") boolean status);

}
