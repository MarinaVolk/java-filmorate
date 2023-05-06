package ru.yandex.practicum.filmorate.controller;/* # parse("File Header.java")*/

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * File Name: FilmController.java
 * Author: Marina Volkova
 * Date: 2023-05-04,   9:04 PM (UTC+3)
 * Description:
 */
@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private Map<Integer, Film> films = new ConcurrentHashMap<>();
    FilmValidator validator = new FilmValidator();
    Integer filmId = 0;

    // добавление фильма
    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        log.info("Запрос на добавление фильма - {}", film.getName());
        validator.isValid(film);
        film.setId(++filmId);
        films.put(film.getId(), film);
        return film;
    }

    // обновление фильма
    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.info("Запрос на обновление фильма - {}", film.getName());
        films.put(film.getId(), film);
        return film;
    }

    // получение всех фильмов
    @GetMapping
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

}
