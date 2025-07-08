package com.farmerapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farmerapp.entity.UnverifiedAdmin;
@Repository
public interface UnverifiedAdminRepository extends JpaRepository<UnverifiedAdmin, String>{

	UnverifiedAdmin findByEmail(String email);

}
