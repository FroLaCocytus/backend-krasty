package com.example.server.exception;

public class UserInvalidDataException extends Exception{
    public UserInvalidDataException(String message) {
        super(message);
    }
}
