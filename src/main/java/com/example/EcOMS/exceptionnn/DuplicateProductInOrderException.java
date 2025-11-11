package com.example.EcOMS.exceptionnn;


public class DuplicateProductInOrderException extends RuntimeException {
    public DuplicateProductInOrderException(String message) {
        super(message);
    }
}