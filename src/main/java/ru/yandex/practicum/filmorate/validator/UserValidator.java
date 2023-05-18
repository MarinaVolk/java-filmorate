package ru.yandex.practicum.filmorate.validator;/* # parse("File Header.java")*/

import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

/**
 * File Name: UserValidator.java
 * Author: Marina Volkova
 * Date: 2023-05-04,   10:30 PM (UTC+3)
 * Description:
 */
public class UserValidator {
    public void isValid(User user) throws ValidationException {
        validateEmail(user.getEmail());
        validateLogin(user.getLogin());
        validateBirthDate(user.getBirthday());

        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    private void validateEmail(String email) throws ValidationException {
        if (!StringUtils.hasText(email)) {
            throw new ValidationException("Электронная почта не может быть пустой.");
        }
        if (!email.contains("@")) {
            throw new ValidationException("Электронная почта должна содержать символ \"@\".");
        }
    }

    private void validateLogin(String login) throws ValidationException {
        if (!StringUtils.hasText(login)) {
            throw new ValidationException("Логин не может быть пустым.");
        }
        if (login.contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы.");
        }
    }

    private void validateBirthDate(LocalDate birthDate) throws ValidationException {
        if (birthDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }

}
