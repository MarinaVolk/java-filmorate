package ru.yandex.practicum.filmorate.controller;/* # parse("File Header.java")*/

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    //private Map<Integer, Film> films = new ConcurrentHashMap<>();
    //private FilmValidator validator = new FilmValidator(); // storage
    private InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
    private FilmService filmService = new FilmService(filmStorage);
    //private Integer filmId = 0; // storage

    // добавление фильма
    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        // validator.isValid(film); // storage
        // film.setId(++filmId); // storage
        // films.put(film.getId(), film); // storage
        log.info("Запрос на добавление фильма - {}", film.getName());
        return filmStorage.add(film);
        // return film; // storage
    }

    // обновление фильма
    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        // validator.isValid(film);
        // if (!films.containsKey(film.getId())) {
            // throw new NotFoundException("Такого фильма не сушествует.");
        // }
        //films.remove(film.getId());  // storage
        //films.put(film.getId(), film); // storage
        log.info("Запрос на обновление фильма - {}", film.getName());
        return filmStorage.update(film);
    }

    // PUT /films/{id}/like/{userId}
    @PutMapping("/{id}/like/{userId}")
    public void addLikeToFilm(@PathVariable int id, @PathVariable int userId) {
       filmService.addLike(id, userId);
    }

    // DELETE /films/{id}/like/{userId}
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeToFilm(@PathVariable int id, @PathVariable int userId) {
        filmService.deleteLike(id, userId);
    }

    // получение всех фильмов
    @GetMapping
    public List<Film> getAll() {
        return filmStorage.getAllFilms(); // storage
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        return filmStorage.getFilmById(id);
    }

    // GET /films/popular?count={count}
    @GetMapping("/popular")
    public List<Film> topFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        log.info("Получен запрос на получение топ фильмов count = {}", count);
        List<Film> topCountFilms = new ArrayList<>(filmService.getTopFilms())
        //List<Film> topCountFilms = filmService.getTopFilms()
        .stream()
                .limit(count)
                .collect(Collectors.toList());

        return topCountFilms;
    }

}
