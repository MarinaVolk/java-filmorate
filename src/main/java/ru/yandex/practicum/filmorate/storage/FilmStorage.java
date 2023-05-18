package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    public Film add(Film film);
    public Film update(Film film);
    public void delete(Integer id);
    public List<Film> getAllFilms();
    public Film getFilmById(Integer id);

}
