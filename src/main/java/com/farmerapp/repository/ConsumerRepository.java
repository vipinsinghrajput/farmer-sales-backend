package com.farmerapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.farmerapp.entity.Consumer;

import jakarta.transaction.Transactional;
@Repository
public interface ConsumerRepository extends JpaRepository<Consumer, Long> ,JpaSpecificationExecutor<Consumer> {

  Optional<Consumer> findByEmail(String email);

boolean existsByEmail(String email);

Optional<Consumer> findByEmailAndStatus(String email, boolean b);

boolean existsByMobileNumber(String mobileNumber);

@Modifying
@Transactional
@Query("UPDATE Consumer c SET c.status = :status WHERE c.id = :id")
int updateStatusById(@Param("id") Long id, @Param("status") boolean status);

}
