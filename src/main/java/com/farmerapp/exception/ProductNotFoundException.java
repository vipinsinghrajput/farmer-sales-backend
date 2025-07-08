package com.farmerapp.exception;

import com.farmerapp.entity.Product;

public class ProductNotFoundException extends RuntimeException {
//    public ProductNotFoundException(Long id) {
//        super("Product not found with ID: " + id);
//    }
    public ProductNotFoundException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ProductNotFoundException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
	
	
	
}
