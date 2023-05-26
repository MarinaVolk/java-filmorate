package ru.yandex.practicum.filmorate.storage.db.dao;/* # parse("File Header.java")*/

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * File Name: MpaDao.java
 * Author: Marina Volkova
 * Date: 2023-05-26,   3:49 PM (UTC+3)
 * Description:
 */
@Component
public class MpaDao {

    private JdbcTemplate jdbcTemplate;

    public MpaDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mpa getMpaById(Integer id) {

        List<Mpa> allMpa = getAllMpa();

        return allMpa.stream().filter(x -> x.getId() == id).findFirst()
        .orElseThrow(() -> new NotFoundException("По данному id " + id + " рейтига mpa не найдено."));
    }

    public List<Mpa> getAllMpa() {

        String sql = "SELECT * FROM mpa";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> setMpa(rs)));
    }

    private Mpa setMpa(ResultSet rs) throws SQLException {
        int id = rs.getInt("rating_id");
        String name = rs.getString("name");
        return new Mpa(id, name);
    }

}
