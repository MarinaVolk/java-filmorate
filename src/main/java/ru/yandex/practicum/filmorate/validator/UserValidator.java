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
    public boolean isValid(User user) throws ValidationException {
        emailValidator(user.getEmail());
        loginValidator(user.getLogin());
        BirthDateValidator(user.getBirthday());

        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        return true;
    }

    private boolean emailValidator(String email) throws ValidationException {
        if (!StringUtils.hasText(email)) {
            throw new ValidationException("Электронная почта не может быть пустой.");
        }
        if (!email.contains("@")) {
            throw new ValidationException("Электронная почта должна содержать символ \"@\".");
        }
        return true;
    }

    private boolean loginValidator(String login) throws ValidationException {
        if (!StringUtils.hasText(login)) {
            throw new ValidationException("Логин не может быть пустым.");
        }
        if (login.contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы.");
        }
        return true;
    }

    private boolean BirthDateValidator(LocalDate birthDate) throws ValidationException {
        if (birthDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        return true;
    }

}
