package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {
    User user;
    UserValidator userValidator;

    @BeforeEach
    void beforeEach() {
        userValidator = new UserValidator();
    }

    @Test
    void validatorWorksCorrectlyWithProperFields() throws ValidationException {
        user = new User("user@gmail.com", "user", LocalDate.of(1999, 01, 01));
        user.setName("Пользователь");

        assertTrue(userValidator.isValid(user));
    }

    @Test
    void shouldThrowExceptionWhenEmailIsEmpty() {
        user = new User("", "user", LocalDate.of(1999, 01, 01));

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userValidator.isValid(user)
        );
        assertEquals("Электронная почта не может быть пустой.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailWithoutAt() {
        user = new User("usergmail.com", "user", LocalDate.of(1999, 01, 01));
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userValidator.isValid(user)
        );
        assertEquals("Электронная почта должна содержать символ \"@\".", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenBirthDateInFuture() {
        user = new User("user@gmail.com", "user", LocalDate.of(2024, 01, 01));
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userValidator.isValid(user)
        );
        assertEquals("Дата рождения не может быть в будущем.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenLoginIsEmpty() {
        user = new User("user@gmail.com", "", LocalDate.of(1999, 01, 01));
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userValidator.isValid(user)
        );
        assertEquals("Логин не может быть пустым.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenLoginHasSpace() {
        user = new User("user@gmail.com", "User login", LocalDate.of(1999, 01, 01));
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userValidator.isValid(user)
        );
        assertEquals("Логин не может содержать пробелы.", exception.getMessage());
    }

    @Test
    void shouldUseLoginIfNameIsEmpty() {
        user = new User("user@gmail.com", "User_login", LocalDate.of(1999, 01, 01));
        user.setName("");

        userValidator.isValid(user);
        assertEquals("User_login", user.getName());
    }

}