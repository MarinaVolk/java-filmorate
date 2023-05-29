package ru.yandex.practicum.filmorate.storage;/* # parse("File Header.java")*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyLikedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * File Name: DbUserStorage.java
 * Author: Marina Volkova
 * Date: 2023-05-26,   3:52 PM (UTC+3)
 * Description:
 */
@Component
@Qualifier("DbUserStorage")
public class DbUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserValidator validator = new UserValidator();

    @Autowired
    public DbUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public User addUser(User user) {
        validator.isValid(user);

        String insertSql = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (?, ?, ?, ?)";
        String selectSql = "SELECT USER_ID FROM USERS WHERE EMAIL = ?";

        // запрос в БД, внесение данных о созданном пользователе
        jdbcTemplate.update(insertSql, user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());

        SqlRowSet rs = jdbcTemplate.queryForRowSet(selectSql, user.getEmail());

        int id = 0;

        if (rs.next()) {
            id = rs.getInt("user_id");
        }
        user.setId(id);

        return user;
    }

    @Override
    public User updateUser(User user) {
        validator.isValid(user);

        if (contains(user.getId())) {

            String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
            // запрос в БД, внесение обновленных данных о пользователе
            jdbcTemplate.update(sql, user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId());
        } else {
            throw new NotFoundException("Пользователя с таким ID не существует.");
        }
        return user;
    }

    @Override
    public void deleteUser(Integer id) {
        String sql = "DELETE FROM USERS WHERE user_id = ?";
        jdbcTemplate.update(sql, id);
    }


    @Override
    public List<User> getAllUsers() {

        String sql = "SELECT * FROM USERS";
        // запрос к БД
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);

        List<User> users = new ArrayList<>();
        try {
            while (rowSet.next()) {
                User user = new User(
                        rowSet.getString("email"),
                        rowSet.getString("login"),
                        rowSet.getDate("birthday").toLocalDate());
                user.setName(rowSet.getString("name"));
                user.setId(rowSet.getInt("user_id"));
                users.add(user);
            }
        } catch (NotFoundException e) {
            System.out.println("Отсутствуют пользователи в БД.");
        }
        return users;
    }

    @Override
    public User getUserById(Integer id) {
        String sql = "SELECT * FROM USERS WHERE user_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        User user;
        if (rowSet.next()) {
            user = new User(
                    rowSet.getString("email"),
                    rowSet.getString("login"),
                    rowSet.getDate("birthday").toLocalDate());
            user.setName(rowSet.getString("name"));
            user.setId(id);
        } else {
            throw new NotFoundException("Отсутствуют данные в БД по указанному ID.");
        }
        user.setFriends(getFriendListById(id));

        return user;
    }

    public Set<Integer> getFriendListById(Integer id) {
        String sql = "SELECT user2_id FROM USER_FRIENDSHIP WHERE user_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);

        Set<Integer> usersIds = new HashSet<>();
        try {
            while (rowSet.next()) {
                Integer newInt = rowSet.getInt("user2_id");
                usersIds.add(newInt);
            }
        } catch (NotFoundException e) {
            System.out.println("Отсутствуют друзья.");
        }
        return usersIds;

    }


    public Set<Integer> getAllLikesByFilmId(Integer filmId) {
        String sql = "SELECT user_id FROM LIKESLIST WHERE film_id = ?";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, filmId);
        Set<Integer> usersIds = new HashSet<>();
        try {
            while (rowSet.next()) {
                Integer newInt = rowSet.getInt("user_id");
                usersIds.add(newInt);
            }
        } catch (NotFoundException e) {
            System.out.println("Отсутствуют лайки к этому фильму.");
        }
        return usersIds;
    }

    public void deleteFromFriendListById(Integer id, Integer friendId) {
        String sql = "DELETE FROM USER_FRIENDSHIP WHERE user_id = ? AND user2_id = ?";
        jdbcTemplate.update(sql, id, friendId);
    }

    // добавление 1 друга в список друзей в БД
    public void addFriend(Integer id, Integer friendId) {
        if (id < 1 || friendId < 1) {
            throw new NotFoundException("ID не может быть отрицательным.");
        }
        Set<Integer> friendsOfUser1 = getFriendListById(id);

        if (friendsOfUser1.contains(friendId)) {
            throw new AlreadyLikedException("Пользователь уже есть в друзьях.");
        }

        String sql = "INSERT INTO USER_FRIENDSHIP (user_id, user2_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, id, friendId);
    }

    private boolean contains(Integer id) {
        String sql = "SELECT * FROM USERS WHERE user_id = ?";
        return jdbcTemplate.queryForRowSet(sql, id).next();
    }

}


