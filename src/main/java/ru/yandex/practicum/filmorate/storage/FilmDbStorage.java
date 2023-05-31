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
 * File Name: DbFilmStorage.java
 * Author: Marina Volkova
 * Date: 2023-05-26,   3:51 PM (UTC+3)
 * Description:
 */
@Component
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final FilmValidator validator = new FilmValidator();

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDbStorage genreDbStorage, MpaDbStorage mpaDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDbStorage = genreDbStorage;
        this.mpaDbStorage = mpaDbStorage;
    }

    @Override
    public Film add(Film film) {
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


    @Override
    public Film update(Film film) {

        if (contains(film.getId())) {
            String sql = "UPDATE films SET name = ?, description = ?, releaseDate = ?, duration = ?, rating_id = ? WHERE film_id = ?";

            jdbcTemplate.update(sql, film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());

            genreDbStorage.saveGenresListByFilm(film);

        } else {
            throw new NotFoundException("Такого фильма не существует.");
        }

        film.getGenres().sort(Comparator.comparingInt(Genre::getId));
        return film;
    }

    @Override
    public void delete(Integer id) {
        String sql1 = "DELETE FROM FILMS WHERE film_id = ?";
        jdbcTemplate.update(sql1, id);

        String sql2 = "DELETE FROM GENRESLIST WHERE film_id = ?";
        jdbcTemplate.update(sql2, id);

        String sql3 = "DELETE FROM LIKESLIST WHERE film_id = ?";
        jdbcTemplate.update(sql3, id);
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = String.format("" +
                "SELECT t.film_id, " +
                "t.name as filmName, " +
                "t.description, t.releaseDate, t.duration, t.rating_id, " +
                "r.rating_id, r.name " +
                "FROM FILMS as t " +
                "LEFT JOIN MPA as r " +
                "ON t.rating_id = r.rating_id ");

        // запрос к БД
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);

        Map<Integer, Film> films = new HashMap<>();

        // получаем список фильмов
        boolean filmFound = false;
        while (rowSet.next()) {
            filmFound = true;
            Film film = new Film(
                    rowSet.getString("filmName"),
                    rowSet.getString("description"),
                    rowSet.getDate("releaseDate").toLocalDate(),
                    rowSet.getInt("duration"));
            film.setId(rowSet.getInt("film_id"));
            Integer mpaId = rowSet.getInt("rating_id");
            String mpaName = rowSet.getString("name");

            film.setMpa(new Mpa(mpaId, mpaName));

            films.put(film.getId(), film);
        }
        // из Map -> List
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(Integer id) {
        String sql = String.format("" +
                "SELECT t.film_id, " +
                "t.name as filmName, " +
                "t.description, t.releaseDate, t.duration, t.rating_id, " +
                "r.rating_id, r.name " +
                "FROM FILMS as t " +
                "LEFT JOIN MPA as r " +
                "ON t.rating_id = r.rating_id " +
                "WHERE t.film_id = (%d)", id);

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        Map<Integer, Film> films = new HashMap<>();
        // получаем фильм
        boolean filmFound = false;
        while (rowSet.next()) {
            filmFound = true;
            Film film = new Film(
                    rowSet.getString("filmName"),
                    rowSet.getString("description"),
                    rowSet.getDate("releaseDate").toLocalDate(),
                    rowSet.getInt("duration"));
            film.setId(rowSet.getInt("film_id"));
            Integer mpaId = rowSet.getInt("rating_id");
            String mpaName = rowSet.getString("name");

            film.setMpa(new Mpa(mpaId, mpaName));
            films.put(film.getId(), film);
        }

        if (films.size() == 0) {
            throw new NotFoundException("Отсутствуют данные в БД по указанному ID");
        }
        return films.get(id);
    }


    @Override
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

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM GENRES";
        // запрос к БД
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);

        List<Genre> genres = new ArrayList<>();
        try {
            while (rowSet.next()) {
                Genre genre = new Genre(
                        rowSet.getInt("genre_id"),
                        rowSet.getString("name"));
                genres.add(genre);
            }
        } catch (NotFoundException e) {
            System.out.println("Отсутствуют жанры в БД.");
        }
        return genres;
    }

    // метод для проставления лайков списку фильмов
    public List<Film> addLikesToListOfFilms(List<Film> films) {
        // накапливаем Ids фильмов
        List<Integer> filmIdList = new ArrayList<>();
        for (Film film : films) {
            filmIdList.add(film.getId());
        }
        // получаем для списка фильмов список лайков
        Map<Integer, Set<Integer>> filmLikes = getLikesSetBySeveralFilmIds(filmIdList);

        for (Film film : films) {
            if (filmLikes.size() > 0) {
                film.setLikes(filmLikes.get(film.getId()));
            } else {
                film.setLikes(new HashSet<>());
            }
        }
        return films;
    }


    // метод для проставления лайков одному фильму
    public Film addLikesToFilms(Film film) {
        Set<Integer> likesIds = getAllLikesByFilmId(film.getId());
        film.setLikes(likesIds);
        return film;
    }


    // получаем все лайки для каждого фильма
    public Map<Integer, Set<Integer>> getLikesSetBySeveralFilmIds(List<Integer> filmIds) {
        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        String sql = String.format("" +
                "SELECT " +
                "t.film_id, t.user_id " +
                "FROM LIKESLIST as t " +
                "WHERE t.film_id IN (%s)", inSql);

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, filmIds.toArray());
        Map<Integer, Set<Integer>> result = new HashMap<>();

        try {
            while (rowSet.next()) {

                Integer filmId = rowSet.getInt("film_id");
                Integer userId = rowSet.getInt("user_id");

                // для каждого нового фильма делаем заглушку - пустое множество лайков
                if (!result.containsKey(filmId)) {
                    Set<Integer> likesList = new HashSet<>();
                    result.put(filmId, likesList);
                }

                // добавляем лайки к фильму
                result.get(filmId).add(userId);
            }
        } catch (NotFoundException e) {
            System.out.println("Отсутствуют лайки у этого фильма.");
        }
        return result;
    }


    public List<Integer> getTopFilms(int count) {
        String sql = "SELECT film_id FROM LIKESLIST GROUP BY film_id ORDER BY COUNT(user_id) DESC LIMIT ?";
        return jdbcTemplate.queryForList(sql, Integer.class, count);
    }

    public List<Integer> getMostPopular() {
        String sql = "SELECT film_id FROM LIKESLIST GROUP BY film_id ORDER BY COUNT(user_id) DESC LIMIT 1";
        return jdbcTemplate.queryForList(sql, Integer.class);
    }

    public void putLikeToFilm(Integer filmId, Integer userId) {
        if (userId < 1 || filmId < 1) {
            throw new NotFoundException("ID не может быть отрицательным.");
        }

        String sql = "INSERT INTO LIKESLIST (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public void dislikeFilm(Integer userId, Integer filmId) {
        String sql = "DELETE FROM LIKESLIST WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    // возвращает список пользователей
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

    // возвращает список фильмов
    public Set<Integer> getAllLikesByUserId(Integer userId) {
        String sql = "SELECT film_id FROM LIKESLIST WHERE user_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);
        Set<Integer> filmsIds = new HashSet<>();
        try {
            while (rowSet.next()) {
                Integer newInt = rowSet.getInt("film_id");
                filmsIds.add(newInt);
            }
        } catch (NotFoundException e) {
            System.out.println("Пользователь не поставил лайки ни одному фильму.");
        }
        return filmsIds;
    }

    private boolean contains(Integer id) {
        String sql = "SELECT * FROM FILMS WHERE film_id = ?";
        return jdbcTemplate.queryForRowSet(sql, id).next();
    }

}


