package ru.yandex.practicum.filmorate.storage;/* # parse("File Header.java")*/

import org.springframework.stereotype.Component;
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

    @Override
    public User addUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
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
        return users.get(id);
    }


}
