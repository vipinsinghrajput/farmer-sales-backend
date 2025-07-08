package com.farmerapp.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmerapp.entity.Address;
import com.farmerapp.entity.Consumer;
import com.farmerapp.entity.DeliveryPerson;
import com.farmerapp.entity.DeliveryPerson.Status;
import com.farmerapp.entity.Farmer;
import com.farmerapp.entity.Notification;
import com.farmerapp.entity.OrderEntity;
import com.farmerapp.entity.OrderItem;
import com.farmerapp.entity.OrderStatus;
import com.farmerapp.entity.Product;
import com.farmerapp.exception.InvalidStatusException;
import com.farmerapp.exception.OrderNotFoundException;
import com.farmerapp.exception.ProductNotFoundException;
import com.farmerapp.exception.UnauthorizedAccessException;
import com.farmerapp.exception.UserNotFoundException;
import com.farmerapp.repository.AddressRepository;
import com.farmerapp.repository.ConsumerRepository;
import com.farmerapp.repository.DeliveryPersonRepository;
import com.farmerapp.repository.FarmerRepository;
import com.farmerapp.repository.NotificationRepository;
import com.farmerapp.repository.OrderRepository;
import com.farmerapp.repository.OtpRepository;
import com.farmerapp.repository.ProductRepository;
import com.farmerapp.request.OrderRequest;
import com.farmerapp.response.ApiResponse;
import com.farmerapp.response.OrderItemResponse;
import com.farmerapp.response.OrderResponse;
import com.farmerapp.response.ProductResponse;
import com.farmerapp.util.AppUtils;
import com.farmerapp.util.JwtUtil;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import com.farmerapp.specification.OrderSpecification;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ConsumerRepository consumerRepository;
	@Autowired
	private DeliveryPersonRepository deliveryPersonRepository;
	@Autowired
	private AddressRepository addressRepository;
	@Autowired
	private DeliveryPersonService deliveryPersonService;
	@Autowired
	private FarmerRepository farmerRepository;
	@Autowired
	private NotificationRepository notificationRepository;
    @Autowired 
    private JwtUtil jwtUtil;
    @Autowired 
	private AppUtils appUtils; 
    
    
    
    public Page<OrderEntity> getFilteredOrders(Long consumerId, OrderStatus status, LocalDate fromDate,LocalDate toDate , int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Specification<OrderEntity> spec = Specification
                .where(OrderSpecification.belongsToConsumer(consumerId))
                .and(OrderSpecification.hasStatus(status))
                .and(OrderSpecification.hasOrderDateRange(fromDate, toDate));

        return orderRepository.findAll(spec, pageable);
    }
    
//	@Transactional
//	public ApiResponse createOrder(OrderRequest orderRequest) {
//	    
//	    Consumer consumer = consumerRepository.findById(orderRequest.getConsumerId())
//	            .orElseThrow(() -> new UserNotFoundException("Consumer not found"));
//
//	    Farmer farmer = farmerRepository.findById(orderRequest.getFarmerId())
//	            .orElseThrow(() -> new UserNotFoundException("Farmer not found"));
//
//	    OrderEntity order = OrderEntity.builder()
//	            .consumer(consumer)
//	            .farmer(farmer)
//	            .totalAmount(0.0)
//	            .deliveryMethod(orderRequest.getDeliveryMethod())
//	            .deliveryAddress(orderRequest.getDeliveryAddress())
//	            .pickupLocation(orderRequest.getPickupLocation())
//	            .status(OrderStatus.PENDING)
//	            .orderDate(LocalDateTime.now())
//	            .updatedDate(LocalDateTime.now())
//	            .orderItems(new ArrayList<>()) 
//	            .build();
//
//	    final double[] totalAmount = {0.0};
//	    
//	    List<OrderItem> orderItems = orderRequest.getItems().stream().map(item -> {
//	        Product product = productRepository.findById(item.getProductId())
//	                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
//
//	        Integer quantity = (item.getQuantity() != null) ? item.getQuantity() : 1;
//
//	        double price = product.getPrice() * quantity;
//	        
//	        totalAmount[0] += price;
//	        
//	        System.err.println(price+"sssssssss");
//	        OrderItem orderItem = OrderItem.builder()
//	                .order(order)
//	                .product(product)
//	                .quantity(quantity)
//	                .price(price)
//	                .build();
//
//	        order.getOrderItems().add(orderItem); 
//	        return orderItem;
//	    }).collect(Collectors.toList());
//         
//	    order.setOrderItems(orderItems);
//         order.setTotalAmount(totalAmount[0]);
//	    OrderEntity savedOrder = orderRepository.save(order);
//
//	    Notification notification = Notification.builder()
//                .farmer(farmer)
//                .isRead(false)
//                .message("New Order Received from " + consumer.getName())
//                .build();
//
//        notificationRepository.save(notification);
//
//	    return ApiResponse.builder()
//	            .message("Order created successfully")
//	            .response(mapToOrderResponse(savedOrder))
//	            .build();
//	}

	
	public ApiResponse getOrderById(Long orderId) {
		OrderEntity order = orderRepository.findById(orderId)
				.orElseThrow(() -> new UserNotFoundException("Order not found with ID: " + orderId));

		
		return ApiResponse.builder().message("Order details retrieved successfully").response(mapToOrderResponse(order))
				.build();
	}
	
	public ApiResponse getDeliveryPersonByOrderId(Long orderId) {
		
		OrderEntity order = orderRepository.findById(orderId)
				.orElseThrow(() -> new UserNotFoundException("Order not found with ID: " + orderId));
		
		 Long deliveryPersonId =  order.getDeliveryPerson().getId();
		  

		return ApiResponse.builder().message("DeliveryPerson details retrieved successfully").response(deliveryPersonService.getById(deliveryPersonId))
				.build();
	}

	// 🔹 **Get All Orders**
	public ApiResponse getAllOrders() {
//		
	        List<OrderEntity> orders = orderRepository.findAll();
		    List<OrderResponse> ordersList = orders.stream().map(this::mapToOrderResponse)
				.collect(Collectors.toList());

		return ApiResponse.builder().message("All orders retrieved successfully").response(ordersList).build();
	}
	
//	 public ApiResponse getFarmerByOrderId(Long orderId) {
//	        
//	    	Farmer farmer = farmerRepository.findByOrderId(orderId)
//	                .orElseThrow(() -> new UserNotFoundException("Farmer Not Found: " + orderidId));
//
//	        return ApiResponse.builder()
//	                .message("Farmer details fetched successfully")
//	                .response(farmer)
//	                .build();
//	    }
	
	public ApiResponse getFarmerOrders(int page, int size, OrderStatus status, String fromDateStr, String toDateStr ) {
		
		 String token = appUtils.getTokenFromHeader();
		    String email = jwtUtil.getUsername(token);
		    
		
	    	Farmer farmer = farmerRepository.findByEmail(email)
	                .orElseThrow(() -> new UserNotFoundException("Farmer Not Found: "));
	    	
	    	  LocalDate fromDate = null;
	    	    LocalDate toDate = null;

	    	    if (fromDateStr != null && !fromDateStr.isBlank()) {
	    	        fromDate = LocalDate.parse(fromDateStr);
	    	    }
	    	    if (toDateStr != null && !toDateStr.isBlank()) {
	    	        toDate = LocalDate.parse(toDateStr);
	    	    }
	    	
	    	
	    	
	        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());

	        Specification<OrderEntity> spec = Specification
	                .where(OrderSpecification.belongsTofarmer(farmer.getId()))
	                .and(OrderSpecification.hasStatus(status))
	                .and(OrderSpecification.hasOrderDateRange(fromDate, toDate));

//	        Page<OrderEntity> orderPage = orderRepository.findAll(spec, pageable);
	     
	        Specification<OrderEntity> distinctSpec = (root, query, cb) -> {
	            query.distinct(true);
	            return spec.toPredicate(root, query, cb);
	        };

	        Page<OrderEntity> orderPage = orderRepository.findAll(distinctSpec, pageable);
	    	

//	    List<OrderEntity> orders = orderRepository.findByFarmerId(farmer.getId());
	    List<OrderResponse> ordersList = orderPage.getContent().stream()
	        .map(this::mapToOrderResponse)
	        .collect(Collectors.toList());

	    Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("orders", ordersList);
        responseMap.put("currentPage", orderPage.getNumber());
        responseMap.put("totalPages", orderPage.getTotalPages());
        responseMap.put("totalElements", orderPage.getTotalElements());
        
	    return ApiResponse.builder()
	        .message("Farmer's orders retrieved successfully")
	        .response(responseMap)
	        .build();
	}

	public ApiResponse getConsumerOrders(int page, int size, OrderStatus status, String toDateStr, String fromDateStr) {
		
		 String token = appUtils.getTokenFromHeader();
		    String email = jwtUtil.getUsername(token);
		    
		
	    	Consumer consumer = consumerRepository.findByEmail(email)
	                .orElseThrow(() -> new UserNotFoundException("Consumer Not Found: "));
	    	
	    	 
	    	  LocalDate fromDate = null;
	    	    LocalDate toDate = null;

	    	    if (fromDateStr != null && !fromDateStr.isBlank()) {
	    	        fromDate = LocalDate.parse(fromDateStr);
	    	    }
	    	    if (toDateStr != null && !toDateStr.isBlank()) {
	    	        toDate = LocalDate.parse(toDateStr);
	    	    }
	    	
	    	
	        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());

	        Specification<OrderEntity> spec = Specification
	                .where(OrderSpecification.belongsToConsumer(consumer.getId()))
	                .and(OrderSpecification.hasStatus(status))
	                .and(OrderSpecification.hasOrderDateRange(fromDate, toDate));

	        Page<OrderEntity> orderPage = orderRepository.findAll(spec, pageable);
	    	
		
	        List<OrderResponse> ordersList = orderPage.getContent().stream()
	                .map(this::mapToOrderResponse)
	                .collect(Collectors.toList());

	        Map<String, Object> responseMap = new HashMap<>();
	        responseMap.put("orders", ordersList);
	        responseMap.put("currentPage", orderPage.getNumber());
	        responseMap.put("totalPages", orderPage.getTotalPages());
	        responseMap.put("totalElements", orderPage.getTotalElements());
	        
//	    List<OrderEntity> orders = orderRepository.findByConsumerId(consumer.getId());
//	    List<OrderResponse> ordersList = orders.stream()
//	        .map(this::mapToOrderResponse)
//	        .collect(Collectors.toList());

	    return ApiResponse.builder()
	        .message("Consumer's orders retrieved successfully")
	        .response(responseMap)
	        .build();
	}
	


//	 @Transactional
//	    public ApiResponse updateOrderStatus(Long orderId, OrderStatus status) {
//	        
//		 OrderEntity order = orderRepository.findById(orderId)
//	                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
//
//	        order.setStatus(status);
//	        orderRepository.save(order);
//	        
//	        Set<Farmer> farmersToNotify = new HashSet<>();
//	        for (OrderItem item : order.getOrderItems()) {
//	            farmersToNotify.add(item.getProduct().getFarmer());
//	        }
//
//	        
//	        for (Farmer farmer : farmersToNotify) {
//	            Notification notification = Notification.builder()
//	                    .farmer(farmer)
//	                    .isRead(false)
//	                    .message("Order #" + order.getId() + " status updated to: " + status)
//	                    .build();
//
//	            notificationRepository.save(notification);
//	        }
//	        
//	        Notification consumerNotification = Notification.builder()
//	                .consumer(order.getConsumer())
//	                .isRead(false)
//	                .message("Your orderId #" + order.getId() + " status has been updated to: " + status)
//	                .build();
//
//	        notificationRepository.save(consumerNotification);
//	        
//	        return ApiResponse.builder()
//	                .message("Order status updated successfully")
//	                .response(mapToOrderResponse(order))
//	                .build();
//	    }


	@Transactional
	public ApiResponse cancelOrder(Long orderId, String reason) {
		
		String token = appUtils.getTokenFromHeader();
	    String  email = jwtUtil.getUsername(token);
	    
	    Consumer consumer = consumerRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Not Authorized "));
	    
	    OrderEntity order = orderRepository.findByIdAndConsumerId(orderId,consumer.getId())
	    	    .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

		if(order.getStatus() == OrderStatus.CANCELED) {
			return ApiResponse.builder().message("Order allready Canceled ").response(null).build();
		}

		 order.setStatus(OrderStatus.CANCELED);
	        orderRepository.save(order);
	       
	        
	        List<OrderItem> items = order.getOrderItems();
	        if (!items.isEmpty()) {
	            Farmer farmer = items.get(0).getProduct().getFarmer();

	            Notification notification = Notification.builder()
	                    .farmer(farmer)
	                    .isRead(false)
	                    .message("Order #" + order.getId() + " CANCELED  Because: " + reason)
	                    .build();

	            notificationRepository.save(notification);
	        }
	        
	        

		return ApiResponse.builder().message("Order Canceled successfully").response(null).build();
	}

	
	//Calculate distance using Haversine formula
	public double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
	    final int R = 6371; // Earth radius in km

	    double latDistance = Math.toRadians(lat2 - lat1);
	    double lonDistance = Math.toRadians(lon2 - lon1);
	    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
	            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
	            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

	    return R * c;
	}

	public double calculateDeliveryFee(double distanceInKm) {
	    if (distanceInKm <= 5) return 20.0;
	    else if (distanceInKm <= 10) return 40.0;
	    else return 50.0 + (distanceInKm - 10) * 5;
	}
	
	public ApiResponse getDeliveryfee(Long productId, Long addressId) {
		
		String token = appUtils.getTokenFromHeader();
	    String  email = jwtUtil.getUsername(token);
	    
	    Consumer consumer = consumerRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Not Authorized "));
	    
	    Product product= productRepository.findById(productId)
	    		.orElseThrow(() -> new ProductNotFoundException("product not found "));
	    
	    Farmer farmer = farmerRepository.findById(product.getFarmer().getId())
	    		.orElseThrow(() -> new UserNotFoundException ("product not found "));
	    
	    Address deliveryAddress = addressRepository.findById(addressId)
	    		.orElseThrow(() -> new UserNotFoundException("address not found "));
	    
	    double distance = calculateDistanceKm(
	            farmer.getLatitude(), farmer.getLongitude(),
	            deliveryAddress.getLatitude(), deliveryAddress.getLongitude()
	    );
	    
	    double deliveryFee = calculateDeliveryFee(distance);
		
		  return ApiResponse.builder().message("Delivery fee ").response(deliveryFee).build();
	}

	public ApiResponse assignDeliveryPerson(Long orderId, Long deliveryPersonId) {
	    OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
	   
	    // Check if delivery person is already assigned
	    if (order.getDeliveryPerson() != null) {
	        throw new OrderNotFoundException("Order already has a delivery person assigned");
	        // Or return an ApiResponse with a meaningful message instead of throwing
	    }
	    
	    DeliveryPerson dp = deliveryPersonRepository.findByIdAndStatus(deliveryPersonId, Status.AVAILABLE)
	    	    .orElseThrow(() -> new UserNotFoundException("Delivery person not found or not available"));

	    Farmer farmer = order.getOrderItems().get(0).getProduct().getFarmer();
	    Address deliveryAddress = order.getDeliveryAddress()
	    		;
	    // Calculate distance
	    double distance = calculateDistanceKm(
	            farmer.getLatitude(), farmer.getLongitude(),
	            deliveryAddress.getLatitude(), deliveryAddress.getLongitude()
	    );

	    // Calculate delivery fee
	    double deliveryFee = calculateDeliveryFee(distance);
	    
	    order.setDeliveryPerson(dp);
	    order.setStatus(OrderStatus.ASSIGNED);
	    order.setDeliveryFee(deliveryFee);
	    dp.setStatus(DeliveryPerson.Status.ASSIGNED);
	    

	    orderRepository.save(order);
	    deliveryPersonRepository.save(dp);
	    return ApiResponse.builder().message("Delivery person assigned").response(mapToOrderResponse(order)).build();
	}

	
	private static final Map<OrderStatus, Set<OrderStatus>> allowedTransitions = Map.of(
		    OrderStatus.PENDING, Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELED),
		    OrderStatus.CONFIRMED, Set.of(OrderStatus.PACKED, OrderStatus.CANCELED),
		    OrderStatus.PACKED, Set.of(OrderStatus.ASSIGNED, OrderStatus.CANCELED),
		    OrderStatus.ASSIGNED, Set.of(OrderStatus.SHIPPED, OrderStatus.CANCELED),
		    OrderStatus.SHIPPED, Set.of(OrderStatus.DELIVERED, OrderStatus.CANCELED),
		    OrderStatus.DELIVERED, Set.of(),   // terminal state
		    OrderStatus.CANCELED, Set.of()    // terminal state
		);

	// Role-based permission check
	private boolean isRoleAllowedToSetStatus(String role, OrderStatus status) {
	    return switch (status) {
	        case PENDING -> "ADMIN".equals(role) || "FARMER".equals(role);
	        case CONFIRMED, PACKED -> "FARMER".equals(role);
	        case SHIPPED, DELIVERED -> "DELIVERY_BOY".equals(role);
	        case CANCELED -> "CONSUMER".equals(role);
	        default -> false;
	    };
	}

	
	public ApiResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
	    
    	String token = appUtils.getTokenFromHeader();
	    String updaterRole = jwtUtil.getRole(token);
		
		OrderEntity order = orderRepository.findById(orderId)
	        .orElseThrow(() -> new OrderNotFoundException("Order not found"));

	    OrderStatus currentStatus = order.getStatus();

	    // Check if newStatus is different
	    if (newStatus == currentStatus) {
	        return ApiResponse.builder()
	                .message("Order status is already " + newStatus)
	                .response(mapToOrderResponse(order))
	                .build();
	    }
	    
	    // Check if transition is allowed (forward only)
	    Set<OrderStatus> allowedNextStatuses = allowedTransitions.getOrDefault(currentStatus, Collections.emptySet());
	    if (!allowedNextStatuses.contains(newStatus)) {
	        throw new InvalidStatusException("Invalid status transition from " + currentStatus + " to " + newStatus);
	    }

	    // Check if updaterRole is allowed to set this newStatus
	    System.out.println(updaterRole);
	    if (!isRoleAllowedToSetStatus(updaterRole, newStatus)) {
	        throw new InvalidStatusException("Role " + updaterRole + " not authorized to set status " + newStatus);
	    }

	    
	    // Update status
	    order.setStatus(newStatus);
	    orderRepository.save(order);
	    
	  
	    if (newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.CANCELED) {

//	    if(newStatus.equals("DELIVERED") || newStatus.equals("CANCELED")) {
	    	  DeliveryPerson d = order.getDeliveryPerson();
	    	d.setStatus(Status.AVAILABLE);
	    	 deliveryPersonRepository.save(d);
	    }

	  
	    
        Set<Farmer> farmersToNotify = new HashSet<>();
        for (OrderItem item : order.getOrderItems()) {
            farmersToNotify.add(item.getProduct().getFarmer());
        }

        
        for (Farmer farmer : farmersToNotify) {
            Notification notification = Notification.builder()
                    .farmer(farmer)
                    .isRead(false)
                    .message("Order #" + order.getId() + " status updated to: " + newStatus)
                    .build();

            notificationRepository.save(notification);
        }
        
        Notification consumerNotification = Notification.builder()
                .consumer(order.getConsumer())
                .isRead(false)
                .message("Your orderId #" + order.getId() + " status has been updated to: " + newStatus)
                .build();

        notificationRepository.save(consumerNotification);
	    

	    return ApiResponse.builder()
	        .message("Order status updated successfully to " + newStatus)
	        .response(mapToOrderResponse(order))
	        .build();
	}

	
	
	private OrderResponse mapToOrderResponse(OrderEntity order) {
		List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
				.map(item -> OrderItemResponse.builder().productId(item.getProduct().getId())
						.productName(item.getProduct().getName()).quantity(item.getQuantity()).price(item.getPrice())
						.build())
				.collect(Collectors.toList());

		return OrderResponse.builder()
				.id(order.getId())
				.consumerId(order.getConsumer().getId())
				.items(itemResponses)
				.totalAmount(order.getTotalAmount())
				.AddressId(order.getDeliveryAddress().getId())
				.status(order.getStatus())
				.orderDate(order.getOrderDate())
				.DeliveryPersonId( order.getDeliveryPerson() != null ? order.getDeliveryPerson().getId() : null)
	            .build();
	}
}
