package ru.practicum.stats.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(final IllegalArgumentException exception) {
        log.warn(exception.getMessage());
        return new ApiError(
                List.of(Arrays.toString(exception.getStackTrace())),
                exception.getMessage(),
                "ILLEGAL ARGUMENT",
                "BAD REQUEST",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleNullPointerException(final NullPointerException exception) {
        log.warn(exception.getMessage());
        return new ApiError(
                List.of(Arrays.toString(exception.getStackTrace())),
                exception.getMessage(),
                "NULL POINTER",
                "BAD REQUEST",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}