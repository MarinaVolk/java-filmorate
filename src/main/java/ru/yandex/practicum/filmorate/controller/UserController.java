package ru.yandex.practicum.filmorate.controller;/* # parse("File Header.java")*/

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * File Name: UserController.java
 * Author: Marina Volkova
 * Date: 2023-05-04,   9:04 PM (UTC+3)
 * Description:
 */
@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor

public class UserController {
    private final UserService userService;

    // создание пользователя
    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Запрос на создание пользователя - {}", user.getEmail());
        return userService.addUser(user);
    }

    // обновление пользователя
    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("Запрос на обновление пользователя - {}", user.getEmail());
        return userService.updateUser(user);
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
        return new ArrayList<>(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUserById(id);
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
