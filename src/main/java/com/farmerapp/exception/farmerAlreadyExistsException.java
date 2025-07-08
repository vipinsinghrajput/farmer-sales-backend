package com.farmerapp.exception;

public class farmerAlreadyExistsException extends RuntimeException {

	public farmerAlreadyExistsException() {
		super();
	}

	public farmerAlreadyExistsException(String message) {
		super(message);
	}
}
