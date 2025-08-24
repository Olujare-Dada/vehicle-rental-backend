package com.bptn.vehicle_project.exception;

public class CustomAccessDeniedException extends RuntimeException {
	
	public CustomAccessDeniedException(String message) {
		super(message);
	}
	
	public CustomAccessDeniedException(String message, Throwable cause) {
		super(message, cause);
	}
}
