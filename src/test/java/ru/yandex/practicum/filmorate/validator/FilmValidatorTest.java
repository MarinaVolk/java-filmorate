package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidatorTest {
    Film film;
    FilmValidator filmValidator;

    @BeforeEach
    void setUp() {
        filmValidator = new FilmValidator();
    }

    @Test
    void shouldThrowExceptionIfNameIsEmpty() throws ValidationException {
        film = new Film("", "Description",
                LocalDate.of(2000, 01, 01), 2000);

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmValidator.isValid(film)
        );
        assertEquals("Название фильма не может быть пустым.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfDescriptionIsEmpty() throws ValidationException {
        film = new Film("Film", "",
                LocalDate.of(2000, 01, 01), 2000);

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmValidator.isValid(film)
        );
        assertEquals("Описание фильма не может быть пустым.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfDescriptionHasMoreThan200Symbols() {
        StringBuilder sb = new StringBuilder();
        String str = "Film Description";
        sb.append(str);
        while (sb.length() < 201) {
            sb.append(str);
        }
        String description = sb.toString();

        film = new Film("film", description,
                LocalDate.of(2000, 01, 01), 2000);

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmValidator.isValid(film)
        );
        assertEquals("Описание фильма не может превышать 200 символов.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenReleaseDateIsBefore28Dec1895() {
        film = new Film("film", "Description",
                LocalDate.of(1895, 12, 27), 2000);

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmValidator.isValid(film)
        );
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfDurationIsNegative() {
        film = new Film("film", "Description",
                LocalDate.of(2000, 01, 01), -2000);

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmValidator.isValid(film)
        );
        assertEquals("Продолжительность фильма должна быть положительной.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfDurationIsZero() {
        Film film = new Film("film", "Description",
                LocalDate.of(2012, 12, 12), 0);

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmValidator.isValid(film)
        );
        assertEquals("Продолжительность фильма должна быть положительной.", exception.getMessage());
    }

}