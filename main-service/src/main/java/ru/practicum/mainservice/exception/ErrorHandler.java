package ru.practicum.mainservice.exception;

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

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleObjectNotFoundException(final ObjectNotFoundException exception) {
        log.warn(exception.getMessage());
        return new ApiError(
                List.of(Arrays.toString(exception.getStackTrace())),
                exception.getMessage(),
                "OBJECT NOT FOUND",
                "NOT FOUND",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @ExceptionHandler(IncorrectFieldException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleIncorrectFieldException(final IncorrectFieldException exception) {
        log.warn(exception.getMessage());
        return new ApiError(List.of(Arrays.toString(exception.getStackTrace())),
                exception.getMessage(),
                "INCORRECT FIELD",
                "CONFLICT",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @ExceptionHandler(WrongConditionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleWrongConditionExceptionException(final WrongConditionException exception) {
        log.warn(exception.getMessage());
        return new ApiError(List.of(Arrays.toString(exception.getStackTrace())),
                exception.getMessage(),
                "WRONG CONDITION",
                "BAD REQUEST",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}