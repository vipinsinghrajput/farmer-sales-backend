package com.farmerapp.service;

import com.farmerapp.payload.CartItemRequest;
import com.farmerapp.entity.*;
import com.farmerapp.exception.OrderNotFoundException;
import com.farmerapp.exception.ProductNotFoundException;
import com.farmerapp.exception.UserNotFoundException;
import com.farmerapp.repository.*;
import com.farmerapp.response.ApiResponse;
import com.farmerapp.response.CartItemResponse;
import com.farmerapp.response.CartResponse;
import com.farmerapp.response.OrderItemSummary;
import com.farmerapp.response.OrderSummaryResponse;
import com.farmerapp.util.AppUtils;
import com.farmerapp.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EnumType;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ConsumerRepository consumerRepository;

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private AddressRepository addressRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private NotificationRepository notificationRepository;
   
    @Autowired
    private AppUtils utils;
    
    @Autowired
    private JwtUtil util;

//    public ApiResponse viewCart(Long consumerId) {
//      
//    	Consumer consumer = consumerRepository.findById(consumerId)
//                .orElseThrow(() -> new UserNotFoundException("Consumer not found"));
//        List<CartItem> cartItem= cartItemRepository.findByConsumer(consumer);
//        List<CartItemResponse> cartItemList= cartItem.stream().map(this::mapToResponse).collect(Collectors.toList());
//        return ApiResponse.builder().message("Cart fetched successfully").response(cartItemList).build();
//    }
    
    public ApiResponse addToCartWithOneQuantity(Long productId) {
        String token = utils.getTokenFromHeader();
        String email = util.getUsername(token);
        Consumer consumer = consumerRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Consumer not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        if (product.getStock() < 0 ) {
            throw new ProductNotFoundException("Not enough stock available!");
        }

        // ✅ Get or create cart
        Cart cart = cartItemRepository.findByConsumer(consumer)
                .orElseGet(() -> cartItemRepository.save(new Cart(consumer)));  

//        double totalWeightKg = 0.0;
//        for (CartItem item : cart.getItems()) {
//            totalWeightKg += item.getProduct().getUnit() * item.getQuantity();
//        }
//
//        // ✅ Add current product weight
//        double newProductWeightKg = product.getWeightPerUnitKg() * request.getQuantity();
//        double projectedWeight = totalWeightKg + newProductWeightKg;
//
//        if (projectedWeight > 40.0) {
//            throw new ProductNotFoundException("Cannot add product: total cart weight exceeds 40kg.");
//        }
        
        
        
        
        String message;

        // ✅ Check if cart has items
        if (!cart.getItems().isEmpty()) {
            Farmer currentFarmer = product.getFarmer();
            Farmer existingFarmer = cart.getItems().get(0).getProduct().getFarmer();

            // ✅ Different farmer -> clear cart and replace
            if (!existingFarmer.getId().equals(currentFarmer.getId())) {
                cart.getItems().clear();
                CartItem newItem = new CartItem(cart, product, 1);
                cart.getItems().add(newItem);
                message = "Cart was replaced because products are from a different farmer.";
            } else {
                // ✅ Same farmer -> update or add
                CartItem existingItem = cart.getItems().stream()
                        .filter(item -> item.getProduct().getId().equals(product.getId()))
                        .findFirst()
                        .orElse(null);

                if (existingItem != null) {
                    existingItem.setQuantity(existingItem.getQuantity() + 1);
                } else {
                    CartItem newItem = new CartItem(cart, product, 1);
                    cart.getItems().add(newItem);
                }
                message = "Product added to cart successfully.";
            }
        } else {
            // ✅ Empty cart
            CartItem newItem = new CartItem(cart, product, 1);
            cart.getItems().add(newItem);
            message = "Product added to cart successfully.";
        }

        
        
        cartItemRepository.save(cart);

        return ApiResponse.builder()
                .message(message)
                .response(mapToCartResponse(cart))
                .build();
    }

    
    
    
    
    public ApiResponse addToCart(CartItemRequest request) {
        String token = utils.getTokenFromHeader();
        String email = util.getUsername(token);
        Consumer consumer = consumerRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Consumer not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if (product.getStock() < request.getQuantity()) {
            throw new ProductNotFoundException("Not enough stock available!");
        }

        // ✅ Get or create cart
        Cart cart = cartItemRepository.findByConsumer(consumer)
                .orElseGet(() -> cartItemRepository.save(new Cart(consumer)));

//        double totalWeightKg = 0.0;
//        for (CartItem item : cart.getItems()) {
//            totalWeightKg += item.getProduct().getUnit() * item.getQuantity();
//        }
//
//        // ✅ Add current product weight
//        double newProductWeightKg = product.getWeightPerUnitKg() * request.getQuantity();
//        double projectedWeight = totalWeightKg + newProductWeightKg;
//
//        if (projectedWeight > 40.0) {
//            throw new ProductNotFoundException("Cannot add product: total cart weight exceeds 40kg.");
//        }
        
        
        
        
        String message;

        // ✅ Check if cart has items
        if (!cart.getItems().isEmpty()) {
            Farmer currentFarmer = product.getFarmer();
            Farmer existingFarmer = cart.getItems().get(0).getProduct().getFarmer();

            // ✅ Different farmer -> clear cart and replace
            if (!existingFarmer.getId().equals(currentFarmer.getId())) {
                cart.getItems().clear();
                CartItem newItem = new CartItem(cart, product, request.getQuantity());
                cart.getItems().add(newItem);
                message = "Cart was replaced because products are from a different farmer.";
            } else {
                // ✅ Same farmer -> update or add
                CartItem existingItem = cart.getItems().stream()
                        .filter(item -> item.getProduct().getId().equals(product.getId()))
                        .findFirst()
                        .orElse(null);

                if (existingItem != null) {
                    existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
                } else {
                    CartItem newItem = new CartItem(cart, product, request.getQuantity());
                    cart.getItems().add(newItem);
                }
                message = "Product added to cart successfully.";
            }
        } else {
            // ✅ Empty cart
            CartItem newItem = new CartItem(cart, product, request.getQuantity());
            cart.getItems().add(newItem);
            message = "Product added to cart successfully.";
        }

        
        
        cartItemRepository.save(cart);

        return ApiResponse.builder()
                .message(message)
                .response(mapToCartResponse(cart))
                .build();
    }

    
    
    

//    @Transactional
//    public ApiResponse addToCart(CartItemRequest request) {
//       
//    	String token =utils.getTokenFromHeader();
//    	String email=util.getUsername(token);
//    	Consumer consumer = consumerRepository.findByEmail(email)
//                .orElseThrow(() -> new UserNotFoundException("Consumer not found "));
//
//
////    	Consumer consumer = consumerRepository.findById(request.getConsumerId())
////                .orElseThrow(() -> new UserNotFoundException("Consumer not found"));
//
//        Product product = productRepository.findById(request.getProductId())
//                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
//
//        if (product.getStock() < request.getQuantity()) {
//            throw new ProductNotFoundException("Not enough stock available!");
//        }
//
//        
//        Cart cart = cartItemRepository.findByConsumer(consumer)
//                .orElseGet(() -> {
//                    Cart newCart = new Cart(consumer);
//                    return cartItemRepository.save(newCart);
//                });
//        
//        CartItem existingItem = cart.getItems().stream()
//              .filter(item -> item.getProduct().equals(product))
//              .findFirst()
//              .orElse(null);
//
//        if (existingItem != null) {
//          existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
//      } else {
//    	  CartItem newItem = new CartItem(cart, product, request.getQuantity());
//          cart.getItems().add(newItem);
//      }
// 
//
//        cartItemRepository.save(cart);
//          
//           return ApiResponse.builder()
//        		   .message("Cart added successfully")
//        		   .response(mapToCartResponse(cart))
//        		   .build();
//    }

    
    
    @Transactional
    public ApiResponse removeItemFromCart(Long productId) {
       
    	  String token = utils.getTokenFromHeader();
          String email = util.getUsername(token);
          Consumer consumer = consumerRepository.findByEmail(email)
                  .orElseThrow(() -> new UserNotFoundException("Consumer not found"));
    	
//    	Consumer consumer = consumerRepository.findById(consumerId)
//                .orElseThrow(() -> new UserNotFoundException("Consumer Not Found"));

        Cart cart = cartItemRepository.findByConsumer(consumer)
                .orElseThrow(() -> new ProductNotFoundException("Cart Not Found"));

        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));

        cartItemRepository.save(cart);

        return ApiResponse.builder()
                .message("Item removed from cart successfully")
                .response(mapToCartResponse(cart))
                .build();
    }
    
    
    
    public ApiResponse decrementCartItemQuantity(Long productId) {
        String token = utils.getTokenFromHeader();
        String email = util.getUsername(token);
        Consumer consumer = consumerRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Consumer not found"));

        Cart cart = cartItemRepository.findByConsumer(consumer)
                .orElseThrow(() -> new ProductNotFoundException("Cart not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException("Product not in cart"));

        String message;

        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
            message = "Product quantity decremented.";
        } else {
            cart.getItems().remove(item);
            message = "Product removed from cart.";
        }

        cartItemRepository.save(cart);

        return ApiResponse.builder()
                .message(message)
                .response(mapToCartResponse(cart))
                .build();
    }

    
    public ApiResponse incrementCartItemQuantity(Long productId) {
        String token = utils.getTokenFromHeader();
        String email = util.getUsername(token);
        Consumer consumer = consumerRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Consumer not found"));

        Cart cart = cartItemRepository.findByConsumer(consumer)
                .orElseThrow(() -> new ProductNotFoundException("Cart not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException("Product not in cart"));

        // ✅ Check stock limit
        if (product.getStock() <= item.getQuantity()) {
            throw new ProductNotFoundException("Cannot add more: stock limit reached.");
        }

        item.setQuantity(item.getQuantity() + 1);
        cartItemRepository.save(cart);

        return ApiResponse.builder()
                .message("Product quantity incremented.")
                .response(mapToCartResponse(cart))
                .build();
    }

    
    @Transactional
    public ApiResponse updateItemQuantity(Long productId, int quantity) {
       
    	 String token = utils.getTokenFromHeader();
         String email = util.getUsername(token);
         Consumer consumer = consumerRepository.findByEmail(email)
                 .orElseThrow(() -> new UserNotFoundException("Consumer not found"));

        Cart cart = cartItemRepository.findByConsumer(consumer)
                .orElseThrow(() -> new ProductNotFoundException("Cart Not Found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if (product.getStock() < quantity) {
            throw new ProductNotFoundException("Not enough stock available!");
        }
       
        CartItem itemToUpdate = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException("Cart item with product not found"));

        itemToUpdate.setQuantity(quantity);
        
        

        cartItemRepository.save(cart);

        return ApiResponse.builder()
                .message("Cart item quantity updated successfully")
                .response(mapToCartResponse(cart))
                .build();
    }
    
    
//    public ApiResponse updateCartItem(Long consumerId, Long productId, int newQuantity) {
//        CartItem item = cartItemRepository.findByConsumerIdAndProductId(consumerId, productId)
//            .orElseThrow(() -> new RuntimeException("Item not found in cart"));
//        item.setQuantity(newQuantity);
//        item.setTotalPrice(newQuantity * item.getTotalPrice()); // Make sure product price is correct here
//        cartRepository.save(item);
//        return ApiResponse.builder().message("Cart item updated").response(item).build();
//    }
    
    @Transactional
    public ApiResponse clearCart(Long consumerId) {
        Consumer consumer = consumerRepository.findById(consumerId)
                .orElseThrow(() -> new UserNotFoundException("Consumer Not Found"));

        Cart cart = cartItemRepository.findByConsumer(consumer)
                .orElseThrow(() -> new ProductNotFoundException("Cart Not Found"));

        cart.getItems().clear();
        cartItemRepository.save(cart);

        return ApiResponse.builder()
                .message("Cart cleared successfully")
                .response(mapToCartResponse(cart))
                .build();
    }
    
    
    public ApiResponse getCartByConsumerId(Long consumerId) {
        Consumer consumer = consumerRepository.findById(consumerId)
                .orElseThrow(() -> new UserNotFoundException("Consumer Not Found"));

        Cart cart = cartItemRepository.findByConsumer(consumer)
                .orElseThrow(() -> new ProductNotFoundException("Cart Not Found"));

        return ApiResponse.builder()
                .message("Cart details fetched successfully")
                .response(mapToCartResponse(cart))
                .build();
    }
    
    public ApiResponse getCart() {
    	
    	 String token = utils.getTokenFromHeader();
         String email = util.getUsername(token);
        
         Consumer consumer = consumerRepository.findByEmail(email)
                 .orElseThrow(() -> new UserNotFoundException("Consumer not found"));

        Cart cart = cartItemRepository.findByConsumer(consumer)
                .orElseThrow(() -> new ProductNotFoundException("Cart Not Found"));

        return ApiResponse.builder()
                .message("Cart details fetched successfully")
                .response(mapToCartResponse(cart))
                .build();
    }
    @Transactional
    public ApiResponse checkout(Long addressId) {
   

    	String token =utils.getTokenFromHeader();
    	String email=util.getUsername(token);
    	Consumer consumer = consumerRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Consumer not found "));

    	
    	
    	//        Consumer consumer = consumerRepository.findById(consumerId)
//                .orElseThrow(() -> new UserNotFoundException("Consumer not found"));

       
        Cart cart = cartItemRepository.findByConsumer(consumer)
                .orElseThrow(() -> new ProductNotFoundException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new ProductNotFoundException("Cart is empty. Add items before checkout.");
        }

      
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new ProductNotFoundException("Insufficient stock for product: " + product.getName());
            }
        }


        Address deliveryAddress ;
       
        if (addressId != null) {
            deliveryAddress = addressRepository.findById(addressId)
                    .orElseThrow(() -> new ProductNotFoundException("Address not found"));
        }else {
        	throw new ProductNotFoundException("Address not found");
        }

     
        OrderEntity order = new OrderEntity();
        order.setConsumer(consumer);
        order.setTotalAmount(0.0);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setOrderDate(LocalDateTime.now());
        order.setDeliveryAddress(deliveryAddress); // Assigning the delivery address

        Set<Farmer> farmersToNotify = new HashSet<>();
       
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem(order, product, cartItem.getQuantity(),product.getPrice());
            orderItems.add(orderItem);
            
            farmersToNotify.add(product.getFarmer());
        }

        order.setOrderItems(orderItems);
        orderRepository.save(order);

        cart.getItems().clear();
        cartItemRepository.save(cart);
        
        
        for (Farmer farmer : farmersToNotify) {
            Notification notification = Notification.builder()
                    .farmer(farmer)
                    .isRead(false)
                    .message("New Order Received from " + consumer.getName())
                    .build();

            notificationRepository.save(notification);
        }
   
        Notification consumerNotification = Notification.builder()
        		.consumer(order.getConsumer())
                .isRead(false)
                .message("Your order #" + order.getId() + " status has been updated to: " + order.getStatus())
                .build();

        notificationRepository.save(consumerNotification);

        return ApiResponse.builder()
                .message("Checkout successful. Order placed!")
                .response(mapToCheckoutResponse(order))
                .build();
    }

    public CartResponse mapToCartResponse(Cart cart) {
      
    	 List<CartItemResponse> items = cart.getItems().stream()
    	            .map(item -> CartItemResponse.builder()
    	                    .productId(item.getProduct().getId())
    	                    .productName(item.getProduct().getName())
    	                    .quantity(item.getQuantity())
    	                    .price(item.getProduct().getPrice())
    	                    .productimageUrl(item.getProduct().getImages())
    	                    .build())
    	            .collect(Collectors.toList());
    	
        return CartResponse.builder()
                .cartId(cart.getId())
                .consumerId(cart.getConsumer().getId())
                .items(items)
                .build();
    }
    
   public OrderSummaryResponse mapToCheckoutResponse(OrderEntity order) {
	   
	   
	    List<OrderItemSummary> itemSummaries = new ArrayList<>();
	    double totalAmount = 0; 
	    
	    for (OrderItem item : order.getOrderItems()) {
	        double subtotal = item.getPrice() * item.getQuantity();
	        totalAmount += subtotal;
	    
	  OrderItemSummary summary = OrderItemSummary.builder()
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .pricePerUnit(item.getPrice())
                .subtotal(subtotal)
                .build();
	  
	
	        itemSummaries.add(summary);
	    }

	    
	    Consumer consumer = order.getConsumer();
	    Address address = order.getDeliveryAddress();

	    String fullAddress = address.getStreet() + ", " + address.getCity() + ", " +
	                         address.getState() + " - " + address.getPostalCode();
	    
	    OrderSummaryResponse response = new OrderSummaryResponse();
	    response.setOrderId(order.getId());
	    response.setConsumerName(consumer.getName());
	    response.setConsumerEmail(consumer.getEmail());
	    response.setDeliveryAddress(fullAddress);
	    response.setItems(itemSummaries);
	    response.setTotalAmount(totalAmount);
	    
	    OrderEntity o = orderRepository.findById(order.getId())
	    		 .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + order.getId()));
	   o.setTotalAmount(totalAmount);
	    return response;
	   }


   
}
