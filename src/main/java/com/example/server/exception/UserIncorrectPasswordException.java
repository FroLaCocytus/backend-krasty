package com.example.server.exception;

public class UserIncorrectPasswordException extends Exception{
    public UserIncorrectPasswordException(String message) {
        super(message);
    }
}
