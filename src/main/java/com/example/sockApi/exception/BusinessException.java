package com.example.sockApi.exception;


public class BusinessException extends Exception {

    public BusinessException(String errorMessage) {
        super(errorMessage);
    }

    public BusinessException(String errorMessage, Exception cause) {
        super(errorMessage, cause);
    }
}
