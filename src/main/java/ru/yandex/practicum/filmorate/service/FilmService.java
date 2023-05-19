package ru.yandex.practicum.filmorate.service;/* # parse("File Header.java")*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyLikedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
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
    private final FilmStorage filmStorage;
    private final FilmValidator filmValidator;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
        filmValidator = new FilmValidator();
    }

    public void addLike(Integer filmId, Integer userId) {
        if (filmStorage.getFilmById(filmId) == null) {
            throw new NotFoundException("Такого фильма не существует.");
        }
        Film film = filmStorage.getFilmById(filmId);
        Set<Integer> likes = film.getLikes();

        if (likes.contains(userId)) {
            throw new AlreadyLikedException("Пользователь уже поставил лайк этому фильму.");
        }
        likes.add(userId);
        filmStorage.update(film);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        if (filmStorage.getFilmById(filmId) == null) {
            throw new NotFoundException("Такого фильма не существует.");
        }
        Film film = filmStorage.getFilmById(filmId);

        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException("Этот пользователь не ставил лайк этому фильму.");
        }
        film.getLikes().remove(userId);
        filmStorage.update(film);
    }

    public List<Film> getTopFilms(int count) {
        List<Film> allFilms = filmStorage.getAllFilms();
        Collections.sort(allFilms, new Comparator<Film>() {
            @Override
            public int compare(Film o1, Film o2) {
                return o2.getLikes().size() - o1.getLikes().size();
            }
        });
        List<Film> top10Films = allFilms
                .stream()
                .limit(count)
                .collect(Collectors.toList());
        return top10Films;
    }

}
