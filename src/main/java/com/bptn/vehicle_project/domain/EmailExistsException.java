package com.bptn.vehicle_project.domain;

public class EmailExistsException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public EmailExistsException(String message) {
		super(message);
	
	}
	

}
