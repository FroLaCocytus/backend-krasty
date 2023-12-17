package com.example.server.exception;

public class UserUnauthorizedException extends Exception{
    public UserUnauthorizedException(String message) {
        super(message);
    }
}
