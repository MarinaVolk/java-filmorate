package ru.yandex.practicum.filmorate.storage;/* # parse("File Header.java")*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.dao.LikesListDao;
import ru.yandex.practicum.filmorate.storage.db.dao.UserFriendshipDao;
import ru.yandex.practicum.filmorate.storage.db.dao.UsersDao;

import java.util.ArrayList;
import java.util.List;

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
    private final UsersDao usersDao;
    private final UserFriendshipDao userFriendshipDao;
    private final LikesListDao likesListDao;

    @Autowired
    public DbUserStorage(JdbcTemplate jdbcTemplate, UsersDao usersDao, UserFriendshipDao userFriendshipDao, LikesListDao likesListDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.usersDao = usersDao;
        this.userFriendshipDao = userFriendshipDao;
        this.likesListDao = likesListDao;
    }

    @Override
    public User addUser(User user) {
        User userWithId = usersDao.save(user);
        userFriendshipDao.saveFriendList(userWithId);
        return userWithId;
    }

    @Override
    public User updateUser(User user) {

        if (contains(user.getId())) {

            String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";

            userFriendshipDao.delete(user.getId());
            userFriendshipDao.saveFriendList(user);

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
        usersDao.deleteUserById(id);
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        List<Integer> usersId = jdbcTemplate.query("SELECT user_id FROM USERS", ((rs, rowNum) -> rs.getInt("user_id")));

        for (Integer userId : usersId) {
            users.add(getUserById(userId));
        }
        return users;
    }

    @Override
    public User getUserById(Integer id) {
        User user = usersDao.getUserById(id);
        user.setFriends(userFriendshipDao.getFriendListById(id));
        return user;
    }


    public boolean contains(Integer id) {
        String sql = "SELECT * FROM USERS WHERE user_id = ?";
        return jdbcTemplate.queryForRowSet(sql, id).next();
    }

}
