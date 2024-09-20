package com.example.demo.exception.exceptions;

public class UsernameDuplicatedException extends RuntimeException {

    public UsernameDuplicatedException(String message) {
        super(message);
    }

}
