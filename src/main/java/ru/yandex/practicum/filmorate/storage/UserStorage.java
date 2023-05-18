package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    public User addUser(User user);
    public User updateUser(User user);
    public void deleteUser(Integer id);
    public List<User> getAllUsers();
    public User getUserById(Integer id);
}
