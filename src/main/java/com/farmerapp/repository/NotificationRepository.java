package com.farmerapp.repository;

import com.farmerapp.entity.Farmer;
import com.farmerapp.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findByFarmerId(Long farmerId);

	List<Notification> findByConsumerId(Long consumerId);
    
	List<Notification> findByFarmerIdAndIsReadFalse(Long farmerId);

	List<Notification> findByConsumerIdAndIsReadFalse(Long consumerId);

	Optional<Notification> findByIdAndFarmerIdAndIsReadFalse(Long notificationId, Long id);

	Optional<Notification> findByIdAndConsumerIdAndIsReadFalse(Long notificationId, Long id);

	List<Notification> findByAdminId(Long adminId);

	List<Notification> findByAdminIdAndIsReadFalse(Long adminId);

	Optional<Notification> findByIdAndAdminIdAndIsReadFalse(Long notificationId, Long id);

	
}
