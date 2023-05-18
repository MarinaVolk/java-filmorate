package ru.yandex.practicum.filmorate.controller;/* # parse("File Header.java")*/

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
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
    private UserValidator validator = new UserValidator();
    private InMemoryUserStorage userStorage = new InMemoryUserStorage();
    private UserService userService;
    private Integer userId = 0;

    // создание пользователя
    @PostMapping
    public User createUser(@RequestBody User user) {
        validator.isValid(user);
        user.setId(++userId);
        users.put(user.getId(), user); // storage
        log.info("Запрос на создание пользователя - {}", user.getEmail());
        return user; // storage
    }

    // обновление пользователя
    @PutMapping
    public User updateUser(@RequestBody User user) {
        validator.isValid(user);
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Такого пользователя не сушествует.");
        }
        users.remove(user.getId()); // storage
        users.put(user.getId(), user); // storage
        log.info("Запрос на обновление пользователя - {}", user.getEmail());
        return user; // storage
    }

    // PUT /users/{id}/friends/{friendId}
    @PutMapping("/{id}/friends/{friendId}")
    public void addNewFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }

    // DELETE /users/{id}/friends/{friendId}
    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
    }

    // получение списка всех пользователей
    @GetMapping
    public List<User> getAll() {
        return new ArrayList<>(users.values()); // storage
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userStorage.getUserById(id);
    }

    // GET /users/{id}/friends
    @GetMapping("/{id}/friends")
    public List<User> getFriendsList(@PathVariable int id) {
        return userService.getAllFriendsList(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getCommonFriendsList(id, otherId);
    }

}
