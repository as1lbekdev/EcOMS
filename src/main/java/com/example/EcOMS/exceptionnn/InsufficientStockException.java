package com.example.EcOMS.exceptionnn;



public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}