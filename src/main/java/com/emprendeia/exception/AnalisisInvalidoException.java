package com.emprendeia.exception;

public class AnalisisInvalidoException extends RuntimeException {

    public AnalisisInvalidoException(String message) {
        super(message);
    }

    public AnalisisInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }
}
