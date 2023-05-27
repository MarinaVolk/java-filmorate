package ru.yandex.practicum.filmorate.storage;/* # parse("File Header.java")*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.db.dao.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * File Name: DbFilmStorage.java
 * Author: Marina Volkova
 * Date: 2023-05-26,   3:51 PM (UTC+3)
 * Description:
 */
@Component
@Qualifier("DbFilmStorage")
public class DbFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmsDao filmsDao;
    private final LikesListDao likesListDao;
    private final GenresListDao genresListDao;
    private final MpaDao mpaDao;
    private final GenresDao genresDao;

    @Autowired
    public DbFilmStorage(JdbcTemplate jdbcTemplate,
                         FilmsDao filmsDao,
                         LikesListDao likesListDao,
                         GenresListDao genresListDao,
                         MpaDao mpaDao,
                         GenresDao genresDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmsDao = filmsDao;
        this.likesListDao = likesListDao;
        this.genresListDao = genresListDao;
        this.mpaDao = mpaDao;
        this.genresDao = genresDao;
    }

    @Override
    public Film add(Film film) {
        Film filmWithId = filmsDao.save(film);
        filmWithId.setMpa(mpaDao.getMpaById(filmWithId.getMpa().getId()));
        genresListDao.saveGenresListByFilm(filmWithId);
        likesListDao.saveLikesListByFilm(filmWithId);
        return filmWithId;
    }

    @Override
    public Film update(Film film) {

        if (contains(film.getId())) {

            String sql = "UPDATE films SET name = ?, description = ?, releaseDate = ?, duration = ?, rating_id = ? WHERE film_id = ?";

            likesListDao.delete(film.getId());
            likesListDao.saveLikesListByFilm(film);

            genresListDao.delete(film.getId());
            genresListDao.saveGenresListByFilm(film);

            jdbcTemplate.update(sql, film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());

        } else {
            throw new NotFoundException("Такого фильма не существует.");
        }

        return film;
    }

    @Override
    public void delete(Integer id) {
        genresListDao.delete(id);
        likesListDao.delete(id);
        filmsDao.deleteFilm(id);
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = new ArrayList<>();

        List<Integer> filmsId = jdbcTemplate.query("SELECT film_id FROM FILMS ORDER BY film_id ASC;", ((rs, rowNum) -> rs.getInt("film_id")));

        for (Integer filmId : filmsId) {
            films.add(getFilmById(filmId));
        }
        return films;
    }

    @Override
    public Film getFilmById(Integer id) {
        Film film = filmsDao.getFilmById(id);

        film.setLikes(likesListDao.getLikesListById(id));

        List<Genre> genres = new ArrayList<>();
        Set<Integer> genresId = genresListDao.getGenresSetIdByFilmId(id);

        if (genresId.size() > 0) {
            genres = new ArrayList<>();
            for (Integer genreId : genresId) {
                genres.add(genresDao.getGenreById(genreId));
            }
        }

        film.setMpa(mpaDao.getMpaById(film.getMpa().getId()));
        film.setGenres(genres);
        return film;
    }


    private boolean contains(Integer id) {
        String sql = "SELECT * FROM FILMS WHERE film_id = ?";
        return jdbcTemplate.queryForRowSet(sql, id).next();
    }

    public Genre getGenreById(Integer id) {
        return genresDao.getGenreById(id);
    }

    public List<Genre> getAllGenres() {
        return genresDao.getAllGenres();
    }

    public Mpa getMpaById(Integer id) {
        return mpaDao.getMpaById(id);
    }

    public List<Mpa> getAllMpa() {
        return mpaDao.getAllMpa();
    }

    public List<Integer> getTopFilms(int count) {
        return likesListDao.getTopFilms(count);
    }

}
