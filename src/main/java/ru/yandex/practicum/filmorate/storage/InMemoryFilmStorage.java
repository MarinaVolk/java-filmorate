package ru.yandex.practicum.filmorate.storage;/* # parse("File Header.java")*/

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

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

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Integer, Film> films = new ConcurrentHashMap<>();

    @Override
    public Film add(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
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
        return films.get(id);
    }

     /*@Override
    public boolean contains(Integer id) {
        return films.containsKey(id);
    }  */

}
