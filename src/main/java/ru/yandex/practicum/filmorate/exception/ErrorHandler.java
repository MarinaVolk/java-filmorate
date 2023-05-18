package ru.yandex.practicum.filmorate.exception;/* # parse("File Header.java")*/

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * File Name: ErrorHandler.java
 * Author: Marina Volkova
 * Date: 2023-05-18,   5:29 PM (UTC+3)
 * Description:
 */
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validationExceptionHandler(final ValidationException e) {
        return new ErrorResponse("Error:" + e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundExceptionHandler(final NotFoundException e) {
        return new ErrorResponse("Error:" + e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse otherExceptionHandler(final AlreadyLikedException e) {
        return new ErrorResponse("Error:" + e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse otherExceptionHandler2(final Exception e) {
        return new ErrorResponse("Error:" + e.getMessage());
    }

}
