package ru.yandex.practicum.filmorate.model;/* # parse("File Header.java")*/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * File Name: Mpa.java
 * Author: Marina Volkova
 * Date: 2023-05-26,   3:32 PM (UTC+3)
 * Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Mpa {
    private int id;
    private String name;

    public Mpa(int id) {
        this.id = id;
    }
}
