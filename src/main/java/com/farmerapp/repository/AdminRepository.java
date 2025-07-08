package com.farmerapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farmerapp.entity.Admin;
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

	boolean existsByEmail(String email);

	Optional<Admin> findByEmail(String email);

	Optional<Admin> findByEmailAndStatus(String email, boolean b);

}
