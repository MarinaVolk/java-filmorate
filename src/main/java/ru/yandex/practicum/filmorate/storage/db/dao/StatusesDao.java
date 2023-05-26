package ru.yandex.practicum.filmorate.storage.db.dao;/* # parse("File Header.java")*/

import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Status;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * File Name: StatusesDao.java
 * Author: Marina Volkova
 * Date: 2023-05-26,   3:50 PM (UTC+3)
 * Description:
 */
public class StatusesDao {
    private JdbcTemplate jdbcTemplate;

    public StatusesDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Status getStatusById(Integer id) {
        List<Status> allStatuses = getAllStatuses();
        return allStatuses.stream().filter(x -> x.getId() == id).findFirst().get();
    }

    public List<Status> getAllStatuses() {
        String sql = "SELECT * FROM STATUSES";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> setStatuses(rs)));
    }

    private Status setStatuses(ResultSet rs) throws SQLException {
        int id = rs.getInt("status_id");
        String name = rs.getString("name");
        return new Status(id, name);
    }

}
