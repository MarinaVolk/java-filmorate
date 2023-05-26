package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.DbFilmStorage;
import ru.yandex.practicum.filmorate.storage.DbUserStorage;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.BooleanSupplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTestDB {
    private final DbFilmStorage filmStorage;

    @Test
    public void testGetAllFilms() {

        Film film = new Film("film", "Description",
                LocalDate.of(2000, 12, 27), 2000);

        filmStorage.getAllMpa();
        //filmStorage.add(film);
        System.out.println(filmStorage.getAllMpa());
        System.out.println(filmStorage.getAllGenres());
    }

}