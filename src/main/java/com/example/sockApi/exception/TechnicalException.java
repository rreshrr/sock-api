package com.example.sockApi.exception;

public class TechnicalException extends Exception {

    public TechnicalException(String errorMessage, Exception cause) {
        super(errorMessage, cause);
    }

}
