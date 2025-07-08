package com.farmerapp.response;

import java.time.LocalDateTime;
import java.util.List;

import com.farmerapp.entity.Address;
import com.farmerapp.entity.DeliveryPerson;
import com.farmerapp.entity.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private Long consumerId;
    private List<OrderItemResponse> items;
    private Double totalAmount;
    private Long AddressId;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private Long DeliveryPersonId;
    
}
