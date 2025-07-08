package com.farmerapp.controller;

import com.farmerapp.entity.Address;
import com.farmerapp.payload.CartItemRequest;
import com.farmerapp.response.ApiResponse;
import com.farmerapp.service.CartService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("cart")
@Validated
public class CartController {

    @Autowired
    private CartService cartService;

    
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addToCart(@Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addToCart(request));
    }
    
    @PostMapping("/addtocart")
    public ResponseEntity<ApiResponse> addToCartWithOneQuantity(@Valid @RequestParam Long productId ) {
        return ResponseEntity.ok(cartService.addToCartWithOneQuantity(productId));
    }
//
    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse> removeItemFromCart(
            @RequestParam @NotNull(message = "Product ID cannot be null") Long productId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(productId));
    }
    
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateItemQuantity(
            @RequestParam @NotNull(message = "Product ID cannot be null") Long productId,
            @RequestParam @Min(value = 1, message = "Quantity must be at least 1") int quantity) {
        return ResponseEntity.ok(cartService.updateItemQuantity( productId, quantity));
    }
    
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse> clearCart(
            @RequestParam @NotNull(message = "Consumer ID cannot be null") Long consumerId) {
        return ResponseEntity.ok(cartService.clearCart(consumerId));
    }
    
    @GetMapping("/view")
    public ResponseEntity<ApiResponse> getCartByConsumerId(
            @RequestParam @NotNull(message = "Consumer ID cannot be null") Long consumerId) {
        return ResponseEntity.ok(cartService.getCartByConsumerId(consumerId));
    }
    
    @GetMapping("/viewcart")
    public ResponseEntity<ApiResponse> getCart() {
        return ResponseEntity.ok(cartService.getCart());
    }
    
    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse> checkout(  @RequestParam Long addressId){
        return ResponseEntity.ok(cartService.checkout(addressId));
    }
    
   
    @PutMapping("/increment")
    public ResponseEntity<ApiResponse> incrementCartItemQuantity(
            @RequestParam @NotNull(message = "Product ID cannot be null") Long productId) {
        return ResponseEntity.ok(cartService.incrementCartItemQuantity(productId));
    }
    
    @PutMapping("/decrement")
    public ResponseEntity<ApiResponse> decrementCartItemQuantity(
            @RequestParam @NotNull(message = "Product ID cannot be null") Long productId) {
        return ResponseEntity.ok(cartService.decrementCartItemQuantity(productId));
    }
    
    
    
}
