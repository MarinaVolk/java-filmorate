package ru.yandex.practicum.filmorate.exception;/* # parse("File Header.java")*/

/**
 * File Name: ValidationException.java
 * Author: Marina Volkova
 * Date: 2023-05-04,   10:09 PM (UTC+3)
 * Description:
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
