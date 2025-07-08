package com.farmerapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "consumer_id", nullable = false, unique = true)
    private Consumer consumer;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cart", orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
    
    

    // ✅ Add a constructor that accepts a Consumer
    public Cart(Consumer consumer) {
        this.consumer = consumer;
        this.items = new ArrayList<>(); // Initialize the list to prevent NullPointerException
    }

    // Add item to cart
//    public void addItem(Product product, int quantity) {
//        CartItem existingItem = items.stream()
//                .filter(item -> item.getProduct().equals(product))
//                .findFirst()
//                .orElse(null);
//
//        if (existingItem != null) {
//            existingItem.setQuantity(existingItem.getQuantity() + quantity);
//        } else {
//            items.add(new CartItem(this, product, quantity));
//        }
//    }
//
//    // Remove item from cart
//    public void removeItem(Product product) {
//        items.removeIf(item -> item.getProduct().equals(product));
//    }
//
//    // Update item quantity in cart
//    public void updateItemQuantity(Product product, int quantity) {
//        items.stream()
//                .filter(item -> item.getProduct().equals(product))
//                .findFirst()
//                .ifPresent(item -> item.setQuantity(quantity));
//    }
//
//    // Clear cart
//    public void clearCart() {
//        items.clear();
//    }
}
