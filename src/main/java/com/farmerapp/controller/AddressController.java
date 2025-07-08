package com.farmerapp.controller;

import com.farmerapp.entity.Address;
import com.farmerapp.response.ApiResponse;
import com.farmerapp.service.AddressService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
@Validated
public class AddressController {

    @Autowired
	private AddressService addressService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> saveAddress(@Valid @RequestBody Address address) {
        return ResponseEntity.ok(addressService.saveAddress(address));
    }

    @GetMapping("/getallconsumeraddress")
    public ResponseEntity<ApiResponse> getAddresses() {
        return ResponseEntity.ok(addressService.getAllAddresses());
    }
    
    @GetMapping("/getbyaddressid")
    public ResponseEntity<ApiResponse> getConsumerAddresses(@NotNull(message = "ConsumerId cannot be null") @RequestParam @Positive(message = "ID must be positive") Long addressId) {
        return ResponseEntity.ok(addressService.getAddressById(addressId));
    }
    
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateAddresses(@NotNull(message = "ConsumerId cannot be null") @RequestParam @Positive(message = "ID must be positive") Long addressId, @Valid @RequestBody Address updatedAddress) {
        return ResponseEntity.ok(addressService.updateAddress(addressId, updatedAddress));
    }
    
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deleteAddresses(@NotNull(message = "ConsumerId cannot be null") @RequestParam @Positive(message = "ID must be positive") Long addressId) {
        return ResponseEntity.ok(addressService.deleteAddress(addressId));
    }
}
