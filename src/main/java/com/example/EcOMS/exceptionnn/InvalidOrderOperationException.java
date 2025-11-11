package com.example.EcOMS.exceptionnn;



public class InvalidOrderOperationException extends RuntimeException {
    public InvalidOrderOperationException(String message) {
        super(message);
    }
}
