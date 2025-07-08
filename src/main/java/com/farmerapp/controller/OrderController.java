package com.farmerapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.farmerapp.entity.OrderEntity;
import com.farmerapp.entity.OrderStatus;
import com.farmerapp.exception.UnauthorizedAccessException;
import com.farmerapp.payload.ProductDto;
import com.farmerapp.request.OrderRequest;
import com.farmerapp.response.ApiResponse;
import com.farmerapp.service.OrderService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@Validated
public class OrderController {

	@Autowired
    private  OrderService orderService;

//    @PostMapping("/create")
//    public ResponseEntity<ApiResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
//        return ResponseEntity.ok(orderService.createOrder(orderRequest));
//    }
    
    
    @GetMapping("/getall")
    public ResponseEntity<ApiResponse> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
    
    @GetMapping("/getfarmerorders")
    public ResponseEntity<ApiResponse> getFarmerOrders(  
    		@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) OrderStatus status, 
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {
        return ResponseEntity.ok(orderService.getFarmerOrders(page, size, status, fromDate,toDate));
    }
    
    
//    @GetMapping("/getconsumerorders")
//    public ResponseEntity<ApiResponse> getConsumerOrders() {
//        return ResponseEntity.ok(orderService.getConsumerOrders());
//    }
    
    @GetMapping("/getconsumerorders")
    public ResponseEntity<ApiResponse> getConsumerOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) OrderStatus status, 
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate
    ) {
        return ResponseEntity.ok(orderService.getConsumerOrders(page, size, status, fromDate,toDate));
    }

    
    @GetMapping("/getbyid")
    public ResponseEntity<ApiResponse> getOrderById(@NotNull(message = "Order ID cannot be null") @RequestParam @Positive(message = "ID must be positive") Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
    
    @GetMapping("/getdeliverypersonbyorderid")
    public ResponseEntity<ApiResponse> getDeliveryPersonByOrderId(@NotNull(message = "Order ID cannot be null") @RequestParam @Positive(message = "ID must be positive") Long id) {
        return ResponseEntity.ok(orderService.getDeliveryPersonByOrderId(id));
    }

    
    @PutMapping("/updatestatus")
    public ResponseEntity<ApiResponse> updateOrderStatus(@NotNull(message = "Order ID cannot be null") @RequestParam @Positive(message = "ID must be positive") Long orderId,@RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

   
    @PutMapping("/cancel")
    public ResponseEntity<ApiResponse> cancelOrder(@NotNull(message = "Order ID cannot be null")@RequestParam @Positive(message = "ID must be positive") Long id ,@NotBlank(message = "Reason cannot be null")@RequestParam  String reason  ) {
        return ResponseEntity.ok(orderService.cancelOrder(id,reason));
    }

    @PostMapping("/assign-delivery")
    public ResponseEntity<ApiResponse> assignDelivery(
    		@NotNull(message = "Order ID cannot be null") @RequestParam  @Positive(message = "ID must be positive") Long orderId,
    		@NotNull(message = "Delivery_Boy ID cannot be null") @RequestParam@Positive(message = "ID must be positive") Long deliveryPersonId) {
        return ResponseEntity.ok(orderService.assignDeliveryPerson(orderId, deliveryPersonId));
    }
    
    @GetMapping("/deliveryfee")
    public ResponseEntity<ApiResponse> getDeliveryfee(
    		@NotNull(message = "Product ID cannot be null") @RequestParam  @Positive(message = "ID must be positive") Long productId,
    		@NotNull(message = "address ID cannot be null") @RequestParam @Positive(message = "ID must be positive") Long addressId) {
        return ResponseEntity.ok(orderService.getDeliveryfee(productId, addressId));
    }


}
