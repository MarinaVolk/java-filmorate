package ru.yandex.practicum.filmorate.storage.db.dao;/* # parse("File Header.java")*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.Set;

/**
 * File Name: UserFriendshipDao.java
 * Author: Marina Volkova
 * Date: 2023-05-26,   3:49 PM (UTC+3)
 * Description:
 */
@Component
public class UserFriendshipDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public UserFriendshipDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Set<Integer> getFriendListById(Integer id) {

        String sql = "SELECT user2_id FROM USER_FRIENDSHIP WHERE user_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("user2_id"), id));

    }

    public void delete(Integer id) {

        String sql = "DELETE FROM USER_FRIENDSHIP WHERE user_id = ?";

        jdbcTemplate.update(sql, id);

    }

    public void saveFriendList(User user) {

        String sql = "INSERT INTO USER_FRIENDSHIP (user_id, user2_id) VALUES (" + user.getId() + ", ?)";

        Set<Integer> friendsId = user.getFriends();

        for (Integer friendId : friendsId) {
            jdbcTemplate.update(sql, friendId);
        }

    }

}
