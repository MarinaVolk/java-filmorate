package ru.yandex.practicum.filmorate.storage;/* # parse("File Header.java")*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.*;

/**
 * File Name: MpaDbStorage.java
 * Author: Marina Volkova
 * Date: 2023-05-30,   1:06 PM (UTC+3)
 * Description:
 */
@Component
@Qualifier("MpaDbStorage")
public class MpaDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmValidator validator = new FilmValidator();

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film add(Film film) {
        return null;
    }

    @Override
    public Film update(Film film) {
        return null;
    }

    @Override
    public void delete(Integer id) {

    }

    @Override
    public List<Film> getAllFilms() {
        return null;
    }

    @Override
    public Film getFilmById(Integer id) {
        return null;
    }

    @Override
    public List<Genre> getAllGenres() {
        return null;
    }

    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa";
        // 1 запрос к БД
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);

        List<Mpa> mpas = new ArrayList<>();
        try {
            while (rowSet.next()) {
                Mpa mpa = new Mpa(
                        rowSet.getInt("rating_id"),
                        rowSet.getString("name"));
                mpas.add(mpa);
            }
        } catch (NotFoundException e) {
            System.out.println("Отсутствуют MPAs в БД.");
        }
        return mpas;
    }

    public Mpa getMpaById(Integer id) {
        String sql = "SELECT * FROM MPA WHERE rating_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        Mpa mpa;
        if (rowSet.next()) {
            mpa = new Mpa(
                    rowSet.getInt("rating_id"),
                    rowSet.getString("name"));
        } else {
            throw new NotFoundException("Отсутствуют данные в БД по указанному ID.");
        }
        return mpa;
    }

    // вынесено из процессора  - для запроса GET
    public List<Film> addMpaToListOfFilms(List<Film> films) {
        // накапливаем Ids фильмов
        List<Integer> filmIdList = new ArrayList<>();
        for (Film film : films) {
            filmIdList.add(film.getId());
        }
        // получаем для списка фильмов список MPA
        // из процессора
        Map<Integer, List<Mpa>> filmMpas = getMpasSetBySeveralFilmIds(filmIdList);

        /// цикл по фильмам - дозаполняем поля...
        for (Film film : films) {
            film.setMpa(filmMpas.get(film.getId()).get(0));
        }
        return films;
    }

    public Film addMpaToFilm(Film film) {
        //String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        String sql = String.format("" +
                "SELECT " +
                "t.film_id, r.rating_id, r.name " +
                "FROM FILMS as t " +
                "LEFT JOIN MPA as r " +
                "ON t.rating_id = r.rating_id " +
                "WHERE t.film_id = (%d)", film.getId());

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        Map<Integer, Mpa> uniqueMPAs = new HashMap<>();
        Map<Integer, List<Mpa>> result = new HashMap<>();

        try {
            while (rowSet.next()) {

                Integer filmId = rowSet.getInt("film_id");
                Integer mpaId = rowSet.getInt("rating_id");
                String mpaName = rowSet.getString("name");

                film.setMpa(new Mpa(mpaId, mpaName));

            }
        } catch (NotFoundException e) {
            throw new NotFoundException("Отсутствуют MPA у этого фильма.");
        }
        return film;
    }


    // получаем все MPA для каждого фильма
    public Map<Integer, List<Mpa>> getMpasSetBySeveralFilmIds(List<Integer> filmIds) {
        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        String sql = String.format("" +
                "SELECT " +
                "t.film_id, r.rating_id, r.name " +
                "FROM FILMS as t " +
                "LEFT JOIN MPA as r " +
                "ON t.rating_id = r.rating_id " +
                "WHERE t.film_id IN (%s)", inSql);

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, filmIds.toArray());
        Map<Integer, Mpa> uniqueMPAs = new HashMap<>();
        Map<Integer, List<Mpa>> result = new HashMap<>();

        try {
            while (rowSet.next()) {

                Integer filmId = rowSet.getInt("film_id");
                Integer mpaId = rowSet.getInt("rating_id");
                String mpaName = rowSet.getString("name");

                // заполняем список уникальных жанров
                // чтобы не плодить экземпляры классов с одинаковым содержанием
                if (!uniqueMPAs.containsKey(mpaId)) {
                    uniqueMPAs.put(mpaId, new Mpa(mpaId, mpaName));
                }

                // для каждого нового фильма делаем заглушку - пустой список MPA
                if (!result.containsKey(filmId)) {
                    List<Mpa> mpaList = new ArrayList<>();
                    result.put(filmId, mpaList);
                }

                // добавляем MPA к фильму
                result.get(filmId).add(uniqueMPAs.get(mpaId));
            }
        } catch (NotFoundException e) {
            System.out.println("Отсутствуют MPA у этого фильма.");
        }
        return result;
    }


}
