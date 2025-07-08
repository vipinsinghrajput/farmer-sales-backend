package com.farmerapp.exception;

public class UnauthorizedAccessException extends Exception {

	public UnauthorizedAccessException() {
		super();
	}

	public UnauthorizedAccessException(String message) {
		super(message);
	}
}
