package ru.yandex.practicum.filmorate.controller;/* # parse("File Header.java")*/

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

/**
 * File Name: GenreController.java
 * Author: Marina Volkova
 * Date: 2023-05-28,   5:45 PM (UTC+3)
 * Description:
 */
@RestController
@RequiredArgsConstructor
public class GenreController {
    private final FilmService filmService;

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        return filmService.findAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable int id) {
        return filmService.getGenreById(id);
    }

}
