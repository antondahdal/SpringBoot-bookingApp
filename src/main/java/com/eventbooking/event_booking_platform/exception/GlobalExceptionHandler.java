package com.eventbooking.event_booking_platform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(ResourceNotFoundException ex) {
        ProblemDetail body = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage());
        body.setTitle("Resource not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }


    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ProblemDetail> handleDuplicate(DuplicateException ex){
        ProblemDetail body = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, ex.getMessage());
        body.setTitle("Duplicate Entity");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ProblemDetail> invalidCredintals(InvalidCredentialsException ex){
        ProblemDetail body = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED, ex.getMessage());
        body.setTitle("Invalid Credintals");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(InsufficientSeatsException .class)
    public ResponseEntity<ProblemDetail> insufficientSeatsException (InsufficientSeatsException  ex){
        ProblemDetail body = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, ex.getMessage());
        body.setTitle("There is No Enough Seats");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ProblemDetail> handleStaleWrite(ObjectOptimisticLockingFailureException ex) {
        ProblemDetail body = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, "The event was updated by someone else. Retry the booking.");
        body.setTitle("Stale event version");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

}
