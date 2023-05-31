package ru.yandex.practicum.filmorate.model;/* # parse("File Header.java")*/

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * File Name: Film.java
 * Author: Marina Volkova
 * Date: 2023-05-04,   8:18 PM (UTC+3)
 * Description:
 */

@Data
@RequiredArgsConstructor
@EqualsAndHashCode
public class Film {
    private Integer id;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final Integer duration;
    private Set<Integer> likes = new HashSet<>();
    private List<Genre> genres = new ArrayList<>();
    private Mpa mpa;
}
