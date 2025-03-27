package com.bptn.vehicle_project.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.bptn.vehicle_project.domain.HttpResponse;
import static org.springframework.http.HttpStatus.FORBIDDEN;




@RestController
@RestControllerAdvice
public class ExceptionHandling implements ErrorController{
	
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String TOKEN_DECODE_ERROR = "Token Decode Error";
	private static final String TOKEN_EXPIRED_ERROR = "Token has Expired";
	private static final String ACCOUNT_LOCKED = "Your account has been locked. Please contact administration";
	private static final String METHOD_IS_NOT_ALLOWED = "This request method is not allowed on this endpoint. Please send a '%s' request";
	private static final String INTERNAL_SERVER_ERROR_MSG = "An error occurred while processing the request";
	private static final String INCORRECT_CREDENTIALS = "Username or Password is Incorrect. Please try again";
	private static final String ACCOUNT_DISABLED = "Your account has been disabled. If this is an error, please contact administration";
	private static final String NOT_ENOUGH_PERMISSION = "You do not have enough permission";
	private static final String NOT_AUTHENTICATED = "You need to log in to access this URL";
	private static final String NO_MAPPING_EXIST_URL = "There is no mapping for this URL";
	private static final String ERROR_PATH = "/error";
	
	
	private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
	    return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus,
	            httpStatus.getReasonPhrase().toUpperCase(), message), httpStatus);
	}
	
	@ExceptionHandler(JWTDecodeException.class)
	public ResponseEntity<HttpResponse> tokenDecodeException() {
	    return this.createHttpResponse(BAD_REQUEST, TOKEN_DECODE_ERROR);
	}
	
	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<HttpResponse> accountDisabledException() {
	    return this.createHttpResponse(BAD_REQUEST, ACCOUNT_DISABLED);
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<HttpResponse> badCredentialsException() {
	    return this.createHttpResponse(BAD_REQUEST, INCORRECT_CREDENTIALS);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<HttpResponse> accessDeniedException() {
	    return this.createHttpResponse(FORBIDDEN, NOT_ENOUGH_PERMISSION);
	}
	
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<HttpResponse> authenticationException() {
	    return this.createHttpResponse(FORBIDDEN, NOT_AUTHENTICATED);
	}
}
