package ru.yandex.practicum.filmorate.validator;/* # parse("File Header.java")*/

import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

/**
 * File Name: FilmValidator.java
 * Author: Marina Volkova
 * Date: 2023-05-04,   10:16 PM (UTC+3)
 * Description:
 */
public class FilmValidator {

    public void isValid(Film film) throws ValidationException {
        validateName(film.getName());
        validateDescription(film.getDescription());
        validateReleaseDate(film.getReleaseDate());
        validateDuration(film.getDuration());
    }

    private void validateName(String name) throws ValidationException {
        if (!StringUtils.hasText(name)) {
            throw new ValidationException("Название фильма не может быть пустым.");
        }
    }

    private void validateDescription(String description) throws ValidationException {
        if (!StringUtils.hasText(description)) {
            throw new ValidationException("Описание фильма не может быть пустым.");
        }
        if (description.length() > 200) {
            throw new ValidationException("Описание фильма не может превышать 200 символов.");
        }
    }

    private void validateReleaseDate(LocalDate releaseDate) throws ValidationException {
        if (releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
        }
    }

    private void validateDuration(int duration) throws ValidationException {
        if (duration <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
    }

}
