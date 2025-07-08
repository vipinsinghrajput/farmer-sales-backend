package com.farmerapp.exception;

public class UnauthorizedStatusUpdateException extends Exception {

	public UnauthorizedStatusUpdateException() {
		super();
	}

	public UnauthorizedStatusUpdateException(String message) {
		super(message);
	}
}
