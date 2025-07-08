//package com.farmerapp.repository;
//
//import com.farmerapp.entity.CartItem;
//import com.farmerapp.entity.Consumer;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//
//public interface CartItemRepository extends JpaRepository<CartItem, Long> {
//    List<CartItem> findByConsumer(Consumer consumer);
//    void deleteByConsumer(Consumer consumer);
//}
package com.farmerapp.repository;

import com.farmerapp.entity.Cart;
import com.farmerapp.entity.Consumer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<Cart, Long> {
    
    // Find cart by consumer
    Optional<Cart> findByConsumer(Consumer consumer);

}
