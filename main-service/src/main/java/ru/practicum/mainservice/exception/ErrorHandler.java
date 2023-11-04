package ru.practicum.mainservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(IncorrectFieldException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIncorrectFieldException(final IncorrectObjectException exception) {
        log.warn(exception.getMessage());
        return new ApiError(List.of(Arrays.toString(exception.getStackTrace())),
                exception.getMessage(),
                "Incorrect input data",
                "BAD_REQUEST",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleIncorrectFieldException(final SQLException exception) {
        log.warn(exception.getMessage());
        return new ApiError(List.of(Arrays.toString(exception.getStackTrace())),
                exception.getMessage(),
                "Integrity constraint has been violated",
                "CONFLICT",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @ExceptionHandler(WrongConditionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleIncorrectObjectException(final WrongConditionException exception) {
        log.warn(exception.getMessage());
        return new ApiError(
                List.of(Arrays.toString(exception.getStackTrace())),
                exception.getMessage(),
                "For the requested operation the conditions are not met",
                "FORBIDDEN",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleIllegalArgumentException(final IllegalArgumentException exception) {
        log.warn(exception.getMessage());
        return new ApiError(
                List.of(Arrays.toString(exception.getStackTrace())),
                exception.getMessage(),
                "Incorrect input parameters",
                "INTERNAL SERVER ERROR",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }
}
