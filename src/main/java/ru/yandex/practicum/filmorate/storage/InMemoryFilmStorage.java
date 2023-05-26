package ru.yandex.practicum.filmorate.storage;/* # parse("File Header.java")*/

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * File Name: InMemoryFilmStorage.java
 * Author: Marina Volkova
 * Date: 2023-05-12,   11:09 PM (UTC+3)
 * Description:
 */

//@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Integer, Film> films = new ConcurrentHashMap<>();
    private Integer filmId = 0;

    @Override
    public Film add(Film film) {
        film.setId(++filmId);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Такого фильма не существует.");
        }
        films.remove(film.getId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void delete(Integer id) {
        films.remove(id);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(Integer id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Такого фильма не существует.");
        }
        return films.get(id);
    }

    public Genre getGenreById(Integer id) {
        return null;
    }

    public List<Genre> getAllGenres() {
        return null;
    }

    public Mpa getMpaById(Integer id) {
        return null;
    }

    public List<Mpa> getAllMpa() {
        return null;
    }

}
