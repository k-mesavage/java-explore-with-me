package ru.practicum.mainservice.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ApiError {

    private final List<String> errors;
    private final String message;
    private final String reason;
    private final String status;
    private final String timestamp;

    public ApiError(List<String> errors, String message, String reason, String status, String timestamp) {
        this.errors = errors;
        this.message = message;
        this.reason = reason;
        this.status = status;
        this.timestamp = timestamp;
    }
}
