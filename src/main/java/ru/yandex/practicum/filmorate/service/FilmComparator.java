package ru.yandex.practicum.filmorate.service;/* # parse("File Header.java")*/

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;

/**
 * File Name: FilmComparator.java
 * Author: Marina Volkova
 * Date: 2023-05-20,   5:45 PM (UTC+3)
 * Description:
 */
public class FilmComparator implements Comparator<Film> {

    @Override
    public int compare(Film o1, Film o2) {
        return o2.getLikes().size() - o1.getLikes().size();
    }
}

