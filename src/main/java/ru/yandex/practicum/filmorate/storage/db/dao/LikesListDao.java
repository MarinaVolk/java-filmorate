package ru.yandex.practicum.filmorate.storage.db.dao;/* # parse("File Header.java")*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * File Name: LikesListDao.java
 * Author: Marina Volkova
 * Date: 2023-05-26,   3:49 PM (UTC+3)
 * Description:
 */
@Component
public class LikesListDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public LikesListDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveLikesListByFilm(Film film) {

        String sql = "INSERT INTO LIKESLIST (film_id, user_id) VALUES (?, ?)";

        Set<Integer> likes = film.getLikes();

        for (Integer like : likes) {
            jdbcTemplate.update(sql, film.getId(), like);
        }

    }

    public Set<Integer> getLikesListById(Integer id) {

        String sql = "SELECT user_id FROM LIKESLIST WHERE film_id = ?";

        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("user_id"), id));

    }

    public void delete(Integer id) {

        String sql = "DELETE FROM LIKESLIST WHERE film_id = ?";

        jdbcTemplate.update(sql, id);

    }

    public List<Integer> getTopFilms(int count) {
        String sql = "SELECT film_id FROM LIKESLIST GROUP BY film_id ORDER BY COUNT(user_id) DESC LIMIT ?";
        return jdbcTemplate.queryForList(sql, Integer.class, count);
    }


    public Set<Integer> getLikesListByUserId(Integer id) {

        String sql = "SELECT film_id FROM LIKESLIST WHERE user_id = ?";

        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("film_id"), id));

    }
}

