package com.farmerapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farmerapp.entity.AuthToken;
@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

}
