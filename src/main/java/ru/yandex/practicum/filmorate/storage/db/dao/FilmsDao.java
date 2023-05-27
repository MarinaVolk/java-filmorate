package ru.yandex.practicum.filmorate.storage.db.dao;/* # parse("File Header.java")*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validator.FilmValidator;


/**
 * File Name: FilmsDao.java
 * Author: Marina Volkova
 * Date: 2023-05-26,   3:48 PM (UTC+3)
 * Description:
 */
@Component
public class FilmsDao {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDao mpaDao;
    private final FilmValidator validator = new FilmValidator();

    @Autowired
    public FilmsDao(JdbcTemplate jdbcTemplate, MpaDao mpaDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDao = new MpaDao(jdbcTemplate);
    }

    public Film save(Film film) {
        validator.isValid(film);

        String insertSql = "INSERT INTO FILMS (NAME, DESCRIPTION, RELEASEDATE, DURATION, RATING_ID) VALUES (?, ?, ?, ?, ?)";

        String selectSql = "SELECT film_id FROM FILMS WHERE NAME = ? AND DESCRIPTION = ? " +
                "AND RELEASEDATE = ? AND DURATION = ?";

        jdbcTemplate.update(insertSql, film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());

        SqlRowSet rs = jdbcTemplate.queryForRowSet(selectSql,
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());

        int id = 0;

        if (rs.next()) {
            id = rs.getInt("film_id");
        }
        film.setId(id);
        return film;
    }

    public Film getFilmById(Integer id) {

        String sql = "SELECT * FROM FILMS WHERE film_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);

        if (rowSet.next()) {

            Film film = new Film(rowSet.getString("name"),
                    rowSet.getString("description"),
                    rowSet.getDate("releaseDate").toLocalDate(),
                    rowSet.getInt("duration"));

            film.setId(rowSet.getInt("film_id"));
            film.setMpa(new Mpa(rowSet.getInt("rating_id")));

            return film;
        } else {
            throw new NotFoundException("Отсутствуют данные в БД по указанному ID");
        }

    }

    public void deleteFilm(Integer id) {
        String sql = "DELETE FROM FILMS WHERE film_id = ?";
        jdbcTemplate.update(sql, id);

    }

}
