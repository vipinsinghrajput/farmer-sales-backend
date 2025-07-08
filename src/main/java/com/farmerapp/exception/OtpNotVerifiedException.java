package com.farmerapp.exception;

public class OtpNotVerifiedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OtpNotVerifiedException() {
		super();
	}

	public OtpNotVerifiedException(String message) {
		super(message);
	}
	
}
