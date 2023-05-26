package ru.yandex.practicum.filmorate.service;/* # parse("File Header.java")*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyLikedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * File Name: UserService.java
 * Author: Marina Volkova
 * Date: 2023-05-12,   11:10 PM (UTC+3)
 * Description:
 * добавление в друзья, удаление из друзей, вывод списка общих друзей.
 */

@Service
public class UserService {
    private final UserStorage userStorage;
    private final UserValidator validator;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
        validator = new UserValidator();
    }

    public User addUser(User user) {
        validator.isValid(user);
        userStorage.addUser(user);
        return user;
    }

    public User updateUser(User user) {
        validator.isValid(user);
        userStorage.updateUser(user);
        return user;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(Integer id) {
        return userStorage.getUserById(id);
    }

    public void addFriend(Integer id, Integer friendId) {
        if (id < 1 || friendId < 1) {
            throw new NotFoundException("ID не может быть отрицательным.");
        }
        User user1 = userStorage.getUserById(id);
        User user2 = userStorage.getUserById(friendId);

        Set<Integer> friendsOfUser1 = user1.getFriends();
        Set<Integer> friendsOfUser2 = user2.getFriends();

        if (friendsOfUser1.contains(friendId)) {
            throw new AlreadyLikedException("Пользователь уже есть в друзьях.");
        }
        friendsOfUser1.add(friendId);
        userStorage.updateUser(user1);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        if (userStorage.getUserById(friendId) == null) {
            throw new NotFoundException("Такого пользователя не существует.");
        }
        User user1 = userStorage.getUserById(id);
        User user2 = userStorage.getUserById(friendId);

        if (!user1.getFriends().contains(friendId)) {
            throw new NotFoundException("Этот пользователь отсутствует в списке друзей.");
        }
        user1.getFriends().remove(friendId);
        user2.getFriends().remove(id);
        userStorage.updateUser(user1);
        userStorage.updateUser(user2);
    }

    public List<User> getAllFriendsList(Integer id) {
        return userStorage.getUserById(id).getFriends().stream()
                .map(friendId -> (userStorage.getUserById(friendId)))
                .collect(Collectors.toList());

    }

    public List<User> getCommonFriendsList(Integer id, Integer otherId) {
        User user1 = userStorage.getUserById(id);
        User user2 = userStorage.getUserById(otherId);

        Set<Integer> friendsOfUser1 = user1.getFriends();
        Set<Integer> friendsOfUser2 = user2.getFriends();
        List<User> commonFriends = new ArrayList<>();

        for (Integer userId : friendsOfUser1) {
            if (friendsOfUser2.contains(userId)) {
                commonFriends.add(userStorage.getUserById(userId));
            }
        }
        return commonFriends;
    }

}
