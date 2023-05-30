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

import java.util.*;
import java.util.stream.Collectors;

/**
 * File Name: GenreDbStorage.java
 * Author: Marina Volkova
 * Date: 2023-05-30,   1:06 PM (UTC+3)
 * Description:
 */
@Component
@Qualifier("GenreDbStorage")
public class GenreDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
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
    public List<Mpa> getAllMpa() {
        return null;
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


    public Genre getGenreById(Integer id) {
        String sql = "SELECT * FROM GENRES WHERE genre_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        Genre genre;
        if (rowSet.next()) {
            genre = new Genre(
                    rowSet.getInt("genre_id"),
                    rowSet.getString("name"));
        } else {
            throw new NotFoundException("Отсутствуют данные в БД по указанному ID.");
        }
        return genre;
    }

    public void deleteFromGenresList(Integer id) {
        String sql = "DELETE FROM GENRESLIST WHERE film_id = ?";
        jdbcTemplate.update(sql, id);
    }


    /**
     * Получаем все жанры для каждого фильма.
     *
     * @param filmIds список фильмов
     * @return Мап film_id: [Genre]
     */
    public Map<Integer, List<Genre>> getGenresSetIdBySeveralFilmIds(List<Integer> filmIds) {
        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        String sql = String.format("" +
                "SELECT t.film_id, t.genre_id, r.name " +
                "FROM GENRESLIST as t " +
                "LEFT JOIN GENRES as r " +
                "ON t.genre_id = r.genre_id " +
                "WHERE t.film_id IN (%s)", inSql);

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, filmIds.toArray());
        Map<Integer, Genre> uniqueGenres = new HashMap<>();
        Map<Integer, List<Genre>> result = new HashMap<>();

        try {
            while (rowSet.next()) {

                Integer filmId = rowSet.getInt("film_id");
                Integer genreId = rowSet.getInt("genre_id");
                String genreName = rowSet.getString("name");

                // заполняем список уникальных жанров
                // чтобы не плодить экземпляры классов с одинаковым содержанием
                if (!uniqueGenres.containsKey(genreId)) {
                    uniqueGenres.put(genreId, new Genre(genreId, genreName));
                }

                // для каждого нового фильма делаем заглушку - пустой список жанров
                if (!result.containsKey(filmId)) {
                    List<Genre> genreList = new ArrayList<>();
                    result.put(filmId, genreList);
                }

                // добавляем жанр к фильму
                result.get(filmId).add(uniqueGenres.get(genreId));
                result.get(filmId).sort(Comparator.comparingInt(Genre::getId));
            }
        } catch (NotFoundException e) {
            System.out.println("Отсутствуют жанры у этого фильма.");
        }
        return result;
    }

    public void saveGenresListByFilm(Film film) {
        List<Integer> filmIds = new ArrayList<>();
        filmIds.add(film.getId());
        // данные о жанрах из БД
        Map<Integer, List<Genre>> mapAsIs = getGenresSetIdBySeveralFilmIds(filmIds);
        List<Genre> genresAsIsList;
        if (mapAsIs.containsKey(film.getId())) {
            genresAsIsList = mapAsIs.get(film.getId());
        } else {
            genresAsIsList = new ArrayList<>();
        }

        Map<Integer, Genre> genresAsIsMap = new HashMap<>();
        for (Genre g : genresAsIsList) {
            genresAsIsMap.put(g.getId(), g);
        }

        // данные о жанрах из полученного на вход фильма с устранением дублей
        List<Genre> genresToBeList = film.getGenres();

        // устранить дубли из листа
        Set<Genre> set = new HashSet<>(genresToBeList);
        genresToBeList.clear();
        genresToBeList.addAll(set);

        Map<Integer, Genre> genresToBeMap = new HashMap<>();
        for (Genre g : genresToBeList) {
            genresToBeMap.put(g.getId(), g);
        }


        Set<Genre> genresToDelete = genresAsIsList
                .stream()
                .filter(e -> !genresToBeMap.containsKey(e.getId()))
                .collect(Collectors.toSet());

        Set<Genre> genresToInsert = genresToBeList
                .stream()
                .filter(e -> !genresAsIsMap.containsKey(e.getId()))
                .collect(Collectors.toSet());

        String sqlInsert = "INSERT INTO GENRESLIST (film_id, genre_id) VALUES (?, ?)";
        String sqlDelete = "DELETE FROM GENRESLIST WHERE genre_id = ? AND film_id = ?";

        for (Genre genre : genresToDelete) {
            jdbcTemplate.update(sqlDelete, genre.getId(), film.getId());
        }

        for (Genre genre : genresToInsert) {
            jdbcTemplate.update(sqlInsert, film.getId(), genre.getId());
        }

    }


    // добавляем жанры к списку фильмов при сборе фильмов из БД (для метода GET)
    public List<Film> addGenresToListOfFilms(List<Film> films) {
        // накапливаем Ids фильмов
        List<Integer> filmIdList = new ArrayList<>();
        for (Film film: films) {
            filmIdList.add(film.getId());
        }
        // получаем для списка фильмов список жанров
        Map<Integer, List<Genre>> filmGenres = getGenresSetIdBySeveralFilmIds(filmIdList);
        // цикл по фильмам - дозаполняем поля...
        for (Film film: films) {
            if (filmGenres.size() > 0) {
                film.setGenres(filmGenres.get(film.getId()));
            } else {
                film.setGenres(new ArrayList<>());
            }
        }
        return films;
}

    /// добавляем жанры к 1 фильму при получении фильма из БД (для метода GET)
    public Film getGenresOfOneFilm(Film film) {
        String sql = String.format("" +
                "SELECT t.film_id, t.genre_id, r.name " +
                "FROM GENRESLIST as t " +
                "LEFT JOIN GENRES as r " +
                "ON t.genre_id = r.genre_id " +
                "WHERE t.film_id IN (%d)", film.getId());

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        Map<Integer, Genre> uniqueGenres = new HashMap<>();
        Map<Integer, List<Genre>> result = new HashMap<>();

        try {
            while (rowSet.next()) {

                Integer filmId = rowSet.getInt("film_id");
                Integer genreId = rowSet.getInt("genre_id");
                String genreName = rowSet.getString("name");

                // заполняем список уникальных жанров
                // чтобы не плодить экземпляры классов с одинаковым содержанием
                if (!uniqueGenres.containsKey(genreId)) {
                    uniqueGenres.put(genreId, new Genre(genreId, genreName));
                }

                // для каждого нового фильма делаем заглушку - пустой список жанров
                if (!result.containsKey(filmId)) {
                    List<Genre> genreList = new ArrayList<>();
                    result.put(filmId, genreList);
                }

                // добавляем жанр к фильму
                result.get(filmId).add(uniqueGenres.get(genreId));
                result.get(filmId).sort(Comparator.comparingInt(Genre::getId));
                List<Genre> genreList = result.get(film.getId());
                film.setGenres(genreList);
            }
        } catch (NotFoundException e) {
            System.out.println("Отсутствуют жанры у этого фильма.");
        }
        return film;
    }

}
