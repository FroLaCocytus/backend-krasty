package com.example.server.exception;

public class OrderAlreadyCreatedException extends Exception{
    public OrderAlreadyCreatedException(String message) {
        super(message);
    }
}
