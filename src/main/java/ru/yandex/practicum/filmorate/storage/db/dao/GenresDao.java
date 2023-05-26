package ru.yandex.practicum.filmorate.storage.db.dao;/* # parse("File Header.java")*/

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * File Name: GenresDao.java
 * Author: Marina Volkova
 * Date: 2023-05-26,   3:48 PM (UTC+3)
 * Description:
 */
@Component
public class GenresDao {

    private JdbcTemplate jdbcTemplate;

    public GenresDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre getGenreById(Integer id) {
        List<Genre> allGenres = getAllGenres();
        return allGenres.stream().filter(x -> x.getId() == id).findFirst().get();
    }

    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM GENRES";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> setGenre(rs)));
    }

    private Genre setGenre(ResultSet rs) throws SQLException {
        int id = rs.getInt("genre_id");
        String name = rs.getString("name");
        return new Genre(id, name);
    }

}
