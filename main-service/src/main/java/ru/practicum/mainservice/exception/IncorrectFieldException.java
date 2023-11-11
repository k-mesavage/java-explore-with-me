package ru.practicum.mainservice.exception;

public class IncorrectFieldException extends RuntimeException {
    public IncorrectFieldException(String message) {
        super(message);
    }
}
