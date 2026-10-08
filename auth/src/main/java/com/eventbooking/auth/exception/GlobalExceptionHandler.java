package com.eventbooking.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ProblemDetail> handleNotFound(ResourceNotFoundException ex) {
		ProblemDetail body = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
		body.setTitle("Resource not found");
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
	}

	@ExceptionHandler(DuplicateException.class)
	public ResponseEntity<ProblemDetail> handleDuplicate(DuplicateException ex) {
		ProblemDetail body = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
		body.setTitle("Duplicate Entity");
		return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ProblemDetail> invalidCredintals(InvalidCredentialsException ex) {
		ProblemDetail body = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
		body.setTitle("Invalid Credintals");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
	}
}
