package com.farmerapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.farmerapp.entity.Address;
import com.farmerapp.entity.OrderEntity;
import com.farmerapp.entity.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {
    List<OrderEntity> findByConsumerId(Long consumerId);
//    List<OrderEntity> findByFarmerId(Long farmerId);

	boolean existsByDeliveryAddressAndStatusNotIn(Address address, List<OrderStatus> of);

//	List<OrderEntity> findByFarmerId(Long id);
	
	@Query("SELECT o FROM OrderEntity o JOIN o.orderItems oi JOIN oi.product p WHERE p.farmer.Id = :farmerId")
	List<OrderEntity> findByFarmerId(@Param("farmerId") Long farmerId);

	Optional<OrderEntity> findByIdAndConsumerId(Long orderId, Long id);
}
