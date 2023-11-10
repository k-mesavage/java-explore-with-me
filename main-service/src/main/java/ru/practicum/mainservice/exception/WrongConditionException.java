package ru.practicum.mainservice.exception;

public class WrongConditionException extends RuntimeException {
    public WrongConditionException(String message) {
        super(message);
    }
}
