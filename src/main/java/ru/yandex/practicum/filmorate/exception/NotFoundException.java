package ru.yandex.practicum.filmorate.exception;/* # parse("File Header.java")*/

/**
 * File Name: NotFoundException.java
 * Author: Marina Volkova
 * Date: 2023-05-09,   12:20 AM (UTC+3)
 * Description:
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
