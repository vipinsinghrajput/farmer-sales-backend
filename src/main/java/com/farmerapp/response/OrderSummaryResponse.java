package com.farmerapp.response;

import java.time.LocalDateTime;
import java.util.List;

import com.farmerapp.entity.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSummaryResponse {

	    private Long orderId;
	    private String consumerName;
	    private String consumerEmail;
	    private List<OrderItemSummary> items;
	    private String deliveryAddress;
	    private double totalAmount;


}
