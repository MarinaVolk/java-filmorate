package ru.yandex.practicum.filmorate.model;/* # parse("File Header.java")*/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * File Name: Status.java
 * Author: Marina Volkova
 * Date: 2023-05-26,   10:51 PM (UTC+3)
 * Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Status {
    private int id;
    private String name;

    public Status(int id) {
        this.id = id;
    }
}
