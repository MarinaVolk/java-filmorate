package ru.yandex.practicum.filmorate.service;/* # parse("File Header.java")*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyLikedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * File Name: FilmService.java
 * Author: Marina Volkova
 * Date: 2023-05-12,   11:10 PM (UTC+3)
 * Description:
 * добавление и удаление лайка, вывод 10 наиболее популярных фильмов по количеству лайков.
 */

@Service
public class FilmService {
    private final FilmDbStorage filmStorage;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final FilmValidator validator;

    @Autowired
    public FilmService(FilmDbStorage filmStorage, GenreDbStorage genreDbStorage, MpaDbStorage mpaDbStorage) {
        this.filmStorage = filmStorage;
        this.genreDbStorage = genreDbStorage;
        this.mpaDbStorage = mpaDbStorage;
        validator = new FilmValidator();
    }

    public Film add(Film film) {
        validator.isValid(film);
        filmStorage.add(film);
        genreDbStorage.saveGenresListByFilm(film);
        return film;
    }

    public Film update(Film film) {
        validator.isValid(film);
        filmStorage.update(film);
        return film;
    }

    public List<Film> getAllFilms() {
        List<Film> films = filmStorage.getAllFilms(); // без проставления доп полей
        // проставление жанров
        genreDbStorage.addGenresToListOfFilms(films);
        // проставление лайков
        filmStorage.addLikesToListOfFilms(films);
        // проставление Мра
        mpaDbStorage.addMpaToListOfFilms(films);

        return films;
    }

    public Film getFilmById(Integer id) {
        Film film = filmStorage.getFilmById(id); // без проставления доп полей
        genreDbStorage.getGenresOfOneFilm(film);
        // проставление лайков
        filmStorage.addLikesToFilms(film);
        // проставление Мра
        mpaDbStorage.addMpaToFilm(film);
        return film;
    }

    public void addLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (filmStorage.getFilmById(filmId) == null) {
            throw new NotFoundException("Такого фильма не существует.");
        }

        Set<Integer> likes = film.getLikes();

        if (likes.contains(userId)) {
            throw new AlreadyLikedException("Пользователь уже поставил лайк этому фильму.");
        }
        filmStorage.putLikeToFilm(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        if (filmStorage.getFilmById(filmId) == null) {
            throw new NotFoundException("Такого фильма не существует.");
        }
        Film film = filmStorage.getFilmById(filmId);
        filmStorage.addLikesToFilms(film); //

        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException("Этот пользователь не ставил лайк этому фильму.");
        }
        filmStorage.dislikeFilm(userId, filmId);
    }

    public List<Film> getTopFilms(int count) {
        List<Film> allFilms = new ArrayList<>();
        if (count == 1) {
            allFilms = getMostPopularFilm();
        } else {
            allFilms = filmStorage.getAllFilms();
            mpaDbStorage.addMpaToListOfFilms(allFilms);
            Collections.sort(allFilms, new FilmComparator());
            List<Film> top10Films = allFilms
                    .stream()
                    .limit(count)
                    .collect(Collectors.toList());
        }
        return allFilms;
    }

    public List<Film> getMostPopularFilm() {
        List<Film> allFilms = new ArrayList<>();
        List<Integer> ids = filmStorage.getMostPopular();
        allFilms.add(filmStorage.getFilmById(ids.get(0)));
        return allFilms;
    }

    public List<Mpa> findAllMpa() {
        return filmStorage.getAllMpa();
    }

    public Mpa getMpaById(Integer id) {
        return mpaDbStorage.getMpaById(id);
    }

    public List<Genre> findAllGenres() {
        return genreDbStorage.getAllGenres();
    }

    public Genre getGenreById(Integer id) {
        List<Genre> allGenres = findAllGenres();
        if (id > allGenres.size() || id < 1) {
            throw new NotFoundException("Некорректный Id жанра");
        }
        return allGenres.stream().filter(x -> x.getId() == id).findFirst().get();
    }

}
