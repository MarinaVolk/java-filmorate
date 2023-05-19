package ru.yandex.practicum.filmorate.storage;/* # parse("File Header.java")*/

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * File Name: InMemoryUserStorage.java
 * Author: Marina Volkova
 * Date: 2023-05-12,   11:08 PM (UTC+3)
 * Description:
 */
@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Integer, User> users = new ConcurrentHashMap<>();
    private UserValidator validator = new UserValidator();
    private Integer userId = 0;

    @Override
    public User addUser(User user) {
        validator.isValid(user);
        user.setId(++userId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        validator.isValid(user);
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Такого пользователя не сушествует.");
        }
        users.remove(user.getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(Integer id) {
        users.remove(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Integer id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Такого пользователя не существует.");
        }
        return user;
    }


}
