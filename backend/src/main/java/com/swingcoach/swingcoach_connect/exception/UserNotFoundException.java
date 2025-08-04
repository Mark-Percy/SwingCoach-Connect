package com.swingcoach.swingcoach_connect.exception;

public class UserNotFoundException extends IllegalStateException{
    public UserNotFoundException(String message) {
        super(message);
    }

}
