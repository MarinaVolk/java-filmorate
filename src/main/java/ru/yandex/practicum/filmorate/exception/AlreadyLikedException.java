package ru.yandex.practicum.filmorate.exception;/* # parse("File Header.java")*/

/**
 * File Name: AlreadyLikedException.java
 * Author: Marina Volkova
 * Date: 2023-05-17,   7:19 PM (UTC+3)
 * Description:
 */
public class AlreadyLikedException extends RuntimeException {
    public AlreadyLikedException(String message) {
        super(message);
    }
}
