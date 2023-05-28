package ru.yandex.practicum.filmorate.controller;/* # parse("File Header.java")*/

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

/**
 * File Name: MpaAndGenresController.java
 * Author: Marina Volkova
 * Date: 2023-05-27,   2:01 AM (UTC+3)
 * Description:
 */

@RestController
@RequiredArgsConstructor
public class MpaController {

    private final FilmService filmService;

    @GetMapping("/mpa/{id}")
    public Mpa getMpaById(@PathVariable int id) {
        return filmService.getMpaById(id);
    }

    @GetMapping("/mpa")
    public List<Mpa> getAllMpa() {
        return filmService.findAllMpa();
    }

}