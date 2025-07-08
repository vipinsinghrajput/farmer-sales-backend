package com.farmerapp.exception;

public class ExpiredTokenException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ExpiredTokenException() {
		super();
	}

	public ExpiredTokenException(String message) {
		super(message);
	}
	
}
