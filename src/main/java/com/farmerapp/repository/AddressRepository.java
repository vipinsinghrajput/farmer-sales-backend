package com.farmerapp.repository;

import com.farmerapp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByConsumerId(Long consumerId);

	Optional<Consumer> findByIdAndConsumerId(Long addressId, Long consumerId);
}
