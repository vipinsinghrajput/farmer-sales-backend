package com.farmerapp.controller;

import com.farmerapp.entity.Notification;
import com.farmerapp.response.ApiResponse;
import com.farmerapp.service.NotificationService;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@Validated
public class NotificationController {

	@Autowired
	private NotificationService notificationService;

	
	public NotificationController() {
		System.out.println("hello");
		// TODO Auto-generated constructor stub
	}

	@GetMapping("/funread")
	public ResponseEntity<ApiResponse> getFarmerUnreadNotifications() {
		return ResponseEntity.ok(notificationService.getFarmerUnreadNotifications());
	}
	
	@GetMapping("/cunread")
	public ResponseEntity<ApiResponse> getConsumerUnreadNotifications() {
		return ResponseEntity.ok(notificationService.getConsumerUnreadNotifications());
	}

	@PutMapping("/fallmarkread")
	public ResponseEntity<ApiResponse> farmerMarkNotificationsAsRead() {
		return ResponseEntity.ok(notificationService.farmerMarkNotificationsAsRead());
	}

	@PutMapping("/callmarkread")
	public ResponseEntity<ApiResponse> markNotificationsAsRead() {
		return ResponseEntity.ok(notificationService.consumerMarkNotificationsAsRead());
	}

	@GetMapping("/farmerall")
	public ResponseEntity<ApiResponse> getAllFarmerNotifications() {
		return ResponseEntity.ok(notificationService.getAllFarmerNotifications());
	}
	
	@GetMapping("/consumerall")
	public ResponseEntity<ApiResponse> getAllConsumerNotifications() {
		return ResponseEntity.ok(notificationService.getAllConsumerNotifications());
	}
	
	@PutMapping("/fmarkread")
	public ResponseEntity<ApiResponse> farmerMarkSingleNotificationAsRead(@NotNull(message = "Notification ID cannot be null") @RequestParam @Positive(message = "ID must be positive") Long notificationId) {
		return ResponseEntity.ok(notificationService.farmerMarkSingleNotificationAsRead(notificationId));
	}
	@PutMapping("/cmarkread")
	public ResponseEntity<ApiResponse> consumerMarkSingleNotificationAsRead(@NotNull(message = "Notification ID cannot be null") @RequestParam @Positive(message = "ID must be positive") Long notificationId) {
		return ResponseEntity.ok(notificationService.consumerMarkSingleNotificationAsRead(notificationId));
	}
	
	@GetMapping("/aunread")
	public ResponseEntity<ApiResponse> getAdminUnreadNotifications() {
		return ResponseEntity.ok(notificationService.getAdminUnreadNotifications());
	}
	
	@PutMapping("/aallmarkread")
	public ResponseEntity<ApiResponse> adminMarkNotificationsAsRead() {
		return ResponseEntity.ok(notificationService.adminMarkNotificationsAsRead());
	}
	
	@GetMapping("/adminall")
	public ResponseEntity<ApiResponse> getAllAdminNotifications() {
		return ResponseEntity.ok(notificationService.getAllAdminNotifications());
	}
	
	@PutMapping("/amarkread")
	public ResponseEntity<ApiResponse> adminMarkSingleNotificationAsRead(@NotNull(message = "Notification ID cannot be null") @RequestParam @Positive(message = "ID must be positive") Long notificationId) {
		return ResponseEntity.ok(notificationService.adminMarkSingleNotificationAsRead(notificationId));
	}
}
