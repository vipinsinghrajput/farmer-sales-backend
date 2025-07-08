package com.farmerapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farmerapp.entity.DeliveryPerson;
import com.farmerapp.entity.Otp;
import com.farmerapp.entity.OtpType;
@Repository
public interface OtpRepository extends JpaRepository<Otp, String> {

	Otp findByEmail(String email);

	Optional<Otp> findByEmailAndType(String email, OtpType register);

}
