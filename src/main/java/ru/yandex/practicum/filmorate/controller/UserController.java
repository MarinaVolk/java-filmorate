package ru.yandex.practicum.filmorate.controller;/* # parse("File Header.java")*/

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * File Name: UserController.java
 * Author: Marina Volkova
 * Date: 2023-05-04,   9:04 PM (UTC+3)
 * Description:
 */
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private Map<Integer, User> users = new ConcurrentHashMap<>();
    UserValidator validator = new UserValidator();
    Integer userId = 0;

    // создание пользователя
    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Запрос на создание пользователя - {}", user.getEmail());
        validator.isValid(user);
        user.setId(++userId);
        users.put(user.getId(), user);
        return user;
    }

    // обновление пользователя
    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("Запрос на обновление пользователя - {}", user.getEmail());
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Такого пользователя не сушествует.");
        }
        users.remove(user.getId());
        users.put(user.getId(), user);
        return user;
    }

    // получение списка всех пользователей
    @GetMapping
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

}
