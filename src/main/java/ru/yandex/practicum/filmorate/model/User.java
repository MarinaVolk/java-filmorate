package ru.yandex.practicum.filmorate.model;/* # parse("File Header.java")*/

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * File Name: User.java
 * Author: Marina Volkova
 * Date: 2023-05-04,   8:18 PM (UTC+3)
 * Description:
 */

@Data
@RequiredArgsConstructor
@EqualsAndHashCode
public class User {
    private Integer id;
    @NonNull
    private String email;
    @NonNull
    private String login;
    private String name;
    @NonNull
    private LocalDate birthday;
    private Set<Integer> friends = new HashSet<>();
    private Set<Integer> filmsLikes = new HashSet<>();
}
