package ru.yandex.practicum.filmorate.service;/* # parse("File Header.java")*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyLikedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.DbFilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.*;

/**
 * File Name: FilmService.java
 * Author: Marina Volkova
 * Date: 2023-05-12,   11:10 PM (UTC+3)
 * Description:
 * добавление и удаление лайка, вывод 10 наиболее популярных фильмов по количеству лайков.
 */

@Service
public class FilmService {
    private final DbFilmStorage filmStorage;
    private final FilmValidator validator;

    @Autowired
    public FilmService(DbFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
        validator = new FilmValidator();
    }

    public Film add(Film film) {
        validator.isValid(film);
        filmStorage.add(film);
        return film;
    }

    public Film update(Film film) {
        validator.isValid(film);
        filmStorage.update(film);
        return film;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
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
        /*likes.add(userId);
        filmStorage.update(film);*/
    }

    public void deleteLike(Integer filmId, Integer userId) {
        if (filmStorage.getFilmById(filmId) == null) {
            throw new NotFoundException("Такого фильма не существует.");
        }
        Film film = filmStorage.getFilmById(filmId);

        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException("Этот пользователь не ставил лайк этому фильму.");
        }
        filmStorage.dislikeFilm(userId, filmId);
        /*film.getLikes().remove(userId);
        filmStorage.update(film);*/
    }

    public List<Film> getTopFilms(int count) {
        List<Film> allFilms = new ArrayList<>();
        List<Integer> topFilmsIds = filmStorage.getTopFilms(count);

        for (Integer filmId: topFilmsIds) {
            //filmStorage.getFilmById(filmId);
            allFilms.add(filmStorage.getFilmById(filmId));
        }

        /*Collections.sort(allFilms, new FilmComparator());
        List<Film> top10Films = allFilms
                .stream()
                .limit(count)
                .collect(Collectors.toList()); */
        return allFilms;
    }

    public List<Mpa> findAllMpa() {
        return filmStorage.getAllMpa();
    }

    public Mpa getMpaById(Integer id) {
        List<Mpa> allMpa = findAllMpa();
        if (id > allMpa.size() || id < 1) {
            throw new NotFoundException("Некорректный Id рейтинга");
        }
        return allMpa.stream().filter(x -> x.getId() == id).findFirst().get();
    }

    public List<Genre> findAllGenres() {
        return filmStorage.getAllGenres();
    }

    public Genre getGenreById(Integer id) {
        List<Genre> allGenres = findAllGenres();
        if (id > allGenres.size() || id < 1) {
            throw new NotFoundException("Некорректный Id жанра");
        }
        return allGenres.stream().filter(x -> x.getId() == id).findFirst().get();
    }

}
