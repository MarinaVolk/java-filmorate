package ru.yandex.practicum.filmorate.storage.db.dao;/* # parse("File Header.java")*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;


/**
 * File Name: UsersDao.java
 * Author: Marina Volkova
 * Date: 2023-05-26,   3:49 PM (UTC+3)
 * Description:
 */
@Component
public class UsersDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UsersDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User getUserById(Integer id) {

        String sql = "SELECT * FROM USERS WHERE user_id = ?";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);

        if (rowSet.next()) {
            User user = new User(rowSet.getString("email"),
                    rowSet.getString("login"),
                    rowSet.getDate("birthday").toLocalDate());
            user.setName(rowSet.getString("name"));
            user.setId(id);
            return user;
        } else {
            throw new NotFoundException("Отсутствуют данные в БД по указанному ID.");
        }
    }

    public void deleteUserById(Integer id) {

        String sql = "DELETE FROM USERS WHERE user_id = ?";

        jdbcTemplate.update(sql, id);

    }

    public User save(User user) {

        String insertSql = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (?, ?, ?, ?)";

        String selectSql = "SELECT USER_ID FROM USERS WHERE EMAIL = ?";

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

    /* common friends
    public List<User> getCommonFriends(Integer userId, Integer friendId) {

        String sql = "SELECT * FROM USERS "+
                "WHERE USER_ID IN (SELECT USER_ID FROM likeslist WHERE USER_ID  = ?) "+
                "INTERSECT "+
                "SELECT * FROM FILMS "+
                "WHERE FILM_ID IN (SELECT FILM_ID FROM likeslist WHERE USER_ID  = ?)";

        List<User> result = new ArrayList<>();

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql,userId,friendId);

        if (rowSet.next()) {
            Film film = new Film(rowSet.getString("name"),
                    rowSet.getString("description"),
                    rowSet.getDate("releaseDate").toLocalDate(),
                    rowSet.getInt("duration"));
            film.setId(rowSet.getInt("film_id"));
            film.setMpa(mpaDao.getMpaById(rowSet.getInt("rating_id")));
            result.add(film);
        }

        return result;
    }  */
}
