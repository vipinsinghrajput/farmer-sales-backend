package com.farmerapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farmerapp.entity.UnverifiedConsumer;
@Repository
public interface UnverifiedConsumerRepository  extends JpaRepository<UnverifiedConsumer, String> {

	UnverifiedConsumer findByEmail(String email);

}
