package ru.yandex.practicum.filmorate.model;/* # parse("File Header.java")*/

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * File Name: Genre.java
 * Author: Marina Volkova
 * Date: 2023-05-26,   3:33 PM (UTC+3)
 * Description:
 */
@Data
//@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Genre {
    private Integer id;
    private String name;

    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
