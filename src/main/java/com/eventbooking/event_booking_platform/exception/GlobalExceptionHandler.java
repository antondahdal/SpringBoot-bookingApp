package com.eventbooking.event_booking_platform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(ResourceNotFoundException ex) {
        ProblemDetail body = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage());
        body.setTitle("Resource not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }


    @ExceptionHandler(InsufficientSeatsException .class)
    public ResponseEntity<ProblemDetail> insufficientSeatsException (InsufficientSeatsException  ex){
        ProblemDetail body = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, ex.getMessage());
        body.setTitle("There is No Enough Seats");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(DownstreamServiceException.class)
    public ResponseEntity<ProblemDetail> handleDownstream(DownstreamServiceException ex) {
        ProblemDetail body = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_GATEWAY, ex.getMessage());
        body.setTitle("Downstream failed");
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ProblemDetail> handleStaleWrite(ObjectOptimisticLockingFailureException ex) {
        ProblemDetail body = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, "The event was updated by someone else. Retry the booking.");
        body.setTitle("Stale event version");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<ProblemDetail> handlenoPermit(CallNotPermittedException  ex) {
        ProblemDetail body = ProblemDetail.forStatusAndDetail(
            HttpStatus.SERVICE_UNAVAILABLE, "The Server is Nor Available");
        body.setTitle("Event unavailable");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    @ExceptionHandler(HoldDataExceedTimeException.class)
    public ResponseEntity<ProblemDetail> handleTimeExceedForHold(ObjectOptimisticLockingFailureException ex) {
        ProblemDetail body = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, "The event is Expired Seats is released.");
        body.setTitle("event is Expired");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

}
