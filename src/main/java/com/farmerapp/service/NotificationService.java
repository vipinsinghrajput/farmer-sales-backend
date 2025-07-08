package com.farmerapp.service;

import com.farmerapp.entity.Admin;
import com.farmerapp.entity.Consumer;
import com.farmerapp.entity.Farmer;
import com.farmerapp.entity.Notification;
import com.farmerapp.exception.NotificationNotFoundException;
import com.farmerapp.exception.UserNotFoundException;
import com.farmerapp.repository.AdminRepository;
import com.farmerapp.repository.ConsumerRepository;
import com.farmerapp.repository.FarmerRepository;
import com.farmerapp.repository.NotificationRepository;
import com.farmerapp.response.ApiResponse;
import com.farmerapp.response.NotificationResponse;
import com.farmerapp.util.AppUtils;
import com.farmerapp.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired 
    private JwtUtil jwtUtil;
    @Autowired 
    private FarmerRepository farmerRepository;
    @Autowired 
    private ConsumerRepository consumerRepository;
    @Autowired 
    private AdminRepository adminRepository;
    @Autowired 
    private AppUtils appUtils;

    public ApiResponse getAllFarmerNotifications() {
      
    	  String token = appUtils.getTokenFromHeader();
  	      String email = jwtUtil.getUsername(token);
  	    
  	      Farmer farmer = farmerRepository.findByEmail(email)
                  .orElseThrow(() -> new UserNotFoundException("Farmer Not Found: "));
      	
    	List<Notification> allNotifications = notificationRepository.findByFarmerId(farmer.getId());
    	List<NotificationResponse> allNotificationsList = allNotifications.stream().map(this::mapToNotificationResponse).collect(Collectors.toList());
        System.out.println(allNotifications+"+++++++++++++");
        if (allNotifications != null && !allNotifications.isEmpty()) {
        	return new ApiResponse().builder().message("All notifications retrieved successfully.").response(allNotificationsList).build();
        } else {
        	return new ApiResponse().builder().message("No notifications found.").build();
        }
    }
    
    public ApiResponse getAllConsumerNotifications() {
    	
    	
    	  String token = appUtils.getTokenFromHeader();
  	      String email = jwtUtil.getUsername(token);
  	    
  	      Consumer consumer= consumerRepository.findByEmail(email)
                  .orElseThrow(() -> new UserNotFoundException("Consumer Not Found: "));
      
        
    	List<Notification> allNotifications = notificationRepository.findByConsumerId(consumer.getId());
    	List<NotificationResponse> allNotificationsList = allNotifications.stream().map(this::mapToNotificationResponse).collect(Collectors.toList());

        if (allNotifications != null && !allNotifications.isEmpty()) {
        	return new ApiResponse().builder().message("All notifications retrieved successfully.").response(allNotificationsList).build();
        } else {
        	return new ApiResponse().builder().message("No notifications found.").build();
        }
    }
    

    public ApiResponse farmerMarkNotificationsAsRead() {
    	
    	  String token = appUtils.getTokenFromHeader();
  	      String email = jwtUtil.getUsername(token);
  	    
  	
      	Farmer farmer = farmerRepository.findByEmail(email)
                  .orElseThrow(() -> new UserNotFoundException("Farmer Not Found: "));
       
    	List<Notification> notifications = notificationRepository.findByFarmerIdAndIsReadFalse(farmer.getId());

        if (notifications != null && !notifications.isEmpty()) {
           
        	notifications.forEach(notification -> notification.setIsRead(true));
            notificationRepository.saveAll(notifications);
            
            return new ApiResponse().builder().message("Notifications marked as read").build();
        }
        
        return new ApiResponse().builder().message("unreadNotifications is Empty").build();
        
    }
    
    public ApiResponse consumerMarkNotificationsAsRead() {
        

  	  String token = appUtils.getTokenFromHeader();
	      String email = jwtUtil.getUsername(token);
	    
	      Consumer consumer= consumerRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Consumer Not Found: "));
    	List<Notification> notifications = notificationRepository.findByConsumerIdAndIsReadFalse(consumer.getId());

        if (notifications != null && !notifications.isEmpty()) {
           
        	notifications.forEach(notification -> notification.setIsRead(true));
            notificationRepository.saveAll(notifications);
            
            return new ApiResponse().builder().message("Notifications marked as read").build();
        }
        
        return new ApiResponse().builder().message("unreadNotifications is Empty").build();
        
    }
   
    public ApiResponse getFarmerUnreadNotifications() {
    	
    	  String token = appUtils.getTokenFromHeader();
  	    String email = jwtUtil.getUsername(token);
  	    
  	
      	Farmer farmer = farmerRepository.findByEmail(email)
                  .orElseThrow(() -> new UserNotFoundException("Farmer Not Found: "));
        
    	List<Notification> unreadNotifications = notificationRepository.findByFarmerIdAndIsReadFalse(farmer.getId());
    	List<NotificationResponse> unreadNotificationsList = unreadNotifications.stream().map(this::mapToNotificationResponse).collect(Collectors.toList());

        if (unreadNotifications != null && !unreadNotifications.isEmpty()) {
            return new ApiResponse().builder().message("Unread notifications retrieved successfully.").response(unreadNotificationsList).build();
        } else {
            return new ApiResponse().builder().message("No unread notifications found.").build();
        }
    }
    
    
    
    
  public ApiResponse getConsumerUnreadNotifications() {
        

	  String token = appUtils.getTokenFromHeader();
	      String email = jwtUtil.getUsername(token);
	    
	      Consumer consumer= consumerRepository.findByEmail(email)
              .orElseThrow(() -> new UserNotFoundException("Consumer Not Found: "));
	      
    	List<Notification> unreadNotifications = notificationRepository.findByConsumerIdAndIsReadFalse(consumer.getId());
    	List<NotificationResponse> unreadNotificationsList = unreadNotifications.stream().map(this::mapToNotificationResponse).collect(Collectors.toList());

        if (unreadNotifications != null && !unreadNotifications.isEmpty()) {
            return new ApiResponse().builder().message("Unread notifications retrieved successfully.").response(unreadNotificationsList).build();
        } else {
            return new ApiResponse().builder().message("No unread notifications found.").build();
        }
    }
  
  
  public ApiResponse farmerMarkSingleNotificationAsRead(Long notificationId) {
	    // Get the token from the header
	    String token = appUtils.getTokenFromHeader();
	    String email = jwtUtil.getUsername(token);

	    // Retrieve the farmer based on the email
	    Farmer farmer = farmerRepository.findByEmail(email)
	            .orElseThrow(() -> new UserNotFoundException("Farmer Not Found: "));

	    // Find the notification by ID and ensure it belongs to the farmer and is unread
	    Notification notification = notificationRepository.findByIdAndFarmerIdAndIsReadFalse(notificationId, farmer.getId())
	            .orElseThrow(() -> new NotificationNotFoundException("Notification not found or already read"));

	    // Mark the notification as read
	    notification.setIsRead(true);
	    notificationRepository.save(notification); // Save the updated notification

	    // Return success response
	    return new ApiResponse().builder().message("Notification marked as read").build();
	}

  public ApiResponse consumerMarkSingleNotificationAsRead(Long notificationId) {
	    // Get the token from the header
	    String token = appUtils.getTokenFromHeader();
	    String email = jwtUtil.getUsername(token);

	    // Retrieve the farmer based on the email
	   Consumer consumer = consumerRepository.findByEmail(email)
	            .orElseThrow(() -> new UserNotFoundException("Consumer Not Found: "));

	    // Find the notification by ID and ensure it belongs to the farmer and is unread
	    Notification notification = notificationRepository.findByIdAndConsumerIdAndIsReadFalse(notificationId, consumer.getId())
	            .orElseThrow(() -> new NotificationNotFoundException("Notification not found or already read"));

	    // Mark the notification as read
	    notification.setIsRead(true);
	    notificationRepository.save(notification); // Save the updated notification

	    // Return success response
	    return new ApiResponse().builder().message("Notification marked as read").build();
	}
  
  
  
  
  public ApiResponse getAllAdminNotifications() {
      
	  String token = appUtils.getTokenFromHeader();
	      String email = jwtUtil.getUsername(token);
	    
	      Admin admin = adminRepository.findByEmail(email)
              .orElseThrow(() -> new UserNotFoundException("Admin Not Found: "));
  	
	List<Notification> allNotifications = notificationRepository.findByAdminId(admin.getId());
	List<NotificationResponse> allNotificationsList = allNotifications.stream().map(this::mapToNotificationResponse).collect(Collectors.toList());
    System.out.println(allNotifications+"+++++++++++++");
    if (allNotifications != null && !allNotifications.isEmpty()) {
    	return new ApiResponse().builder().message("All notifications retrieved successfully.").response(allNotificationsList).build();
    } else {
    	return new ApiResponse().builder().message("No notifications found.").build();
    }
}
  
  
  public ApiResponse adminMarkNotificationsAsRead() {
  	
	  String token = appUtils.getTokenFromHeader();
	      String email = jwtUtil.getUsername(token);
	    
	
  	Admin admin = adminRepository.findByEmail(email)
              .orElseThrow(() -> new UserNotFoundException("Admin Not Found: "));
   
	List<Notification> notifications = notificationRepository.findByAdminIdAndIsReadFalse(admin.getId());

    if (notifications != null && !notifications.isEmpty()) {
       
    	notifications.forEach(notification -> notification.setIsRead(true));
        notificationRepository.saveAll(notifications);
        
        return new ApiResponse().builder().message("Notifications marked as read").build();
    }   
    return new ApiResponse().builder().message("unreadNotifications is Empty").build();
}

  
  public ApiResponse getAdminUnreadNotifications() {
  	
	  String token = appUtils.getTokenFromHeader();
	    String email = jwtUtil.getUsername(token);
	    
	
  	Admin admin = adminRepository.findByEmail(email)
              .orElseThrow(() -> new UserNotFoundException("Admin Not Found: "));
    
	List<Notification> unreadNotifications = notificationRepository.findByAdminIdAndIsReadFalse(admin.getId());
	List<NotificationResponse> unreadNotificationsList = unreadNotifications.stream().map(this::mapToNotificationResponse).collect(Collectors.toList());

    if (unreadNotifications != null && !unreadNotifications.isEmpty()) {
        return new ApiResponse().builder().message("Unread notifications retrieved successfully.").response(unreadNotificationsList).build();
    } else {
        return new ApiResponse().builder().message("No unread notifications found.").build();
    } 
  }
    
    public ApiResponse adminMarkSingleNotificationAsRead(Long notificationId) {
	    // Get the token from the header
	    String token = appUtils.getTokenFromHeader();
	    String email = jwtUtil.getUsername(token);

	    // Retrieve the farmer based on the email
	    Admin admin = adminRepository.findByEmail(email)
	            .orElseThrow(() -> new UserNotFoundException("Admin Not Found: "));

	    // Find the notification by ID and ensure it belongs to the farmer and is unread
	    Notification notification = notificationRepository.findByIdAndAdminIdAndIsReadFalse(notificationId, admin.getId())
	            .orElseThrow(() -> new NotificationNotFoundException("Notification not found or already read"));

	    // Mark the notification as read
	    notification.setIsRead(true);
	    notificationRepository.save(notification); // Save the updated notification

	    // Return success response
	    return new ApiResponse().builder().message("Notification marked as read").build();
	}
  
  
  public NotificationResponse mapToNotificationResponse(Notification notification) {
	   
	  String targetUser = null;

	    if (notification.getFarmer() != null) {
	        targetUser = notification.getFarmer().getName();
	    } else if (notification.getConsumer() != null) {
	        targetUser = notification.getConsumer().getName();
	    }

	    return NotificationResponse.builder()
	            .id(notification.getId())
	            .message(notification.getMessage())
	            .isRead(notification.getIsRead())
	            .createdAt(notification.getCreatedAt())
	            .targetUser(targetUser)
	            .build();
	}
  
}
