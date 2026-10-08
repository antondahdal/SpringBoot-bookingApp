package com.eventbooking.events.exception;


public class InsufficientSeatsException extends RuntimeException{

    public InsufficientSeatsException(String message){
        super(message);
    }
}