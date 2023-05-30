package ru.yandex.practicum.filmorate.dbtests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Qualifier

class FilmDbStorageTest {
    private final UserDbStorage userStorage;
    private final GenreDbStorage genreStorage;
    private final MpaDbStorage mpaDbStorage;
    private final FilmDbStorage filmStorage;

    @Test
    void addShouldAddFilmCorrectly() {
        Film film = new Film("Film1", "Description",
                LocalDate.of(2000, 01, 01), 2000);

        Mpa mpa = new Mpa(1, "Комедия");
        film.setMpa(mpa);

        filmStorage.add(film);
        mpaDbStorage.addMpaToFilm(film);

        Film filmFromDb = filmStorage.getFilmById(1);
        mpaDbStorage.addMpaToFilm(filmFromDb);

        assertEquals(film, filmFromDb);
    }

    @Test
    void updateShouldUpdateFilmCorrectly() {
        Film film = new Film("Film1", "Description",
                LocalDate.of(2000, 01, 01), 2000);
        Mpa mpa = new Mpa(1, "Комедия");
        film.setMpa(mpa);

        filmStorage.add(film);
        mpaDbStorage.addMpaToFilm(film);

        Film updatedFilm = new Film("Film1", "Updated_Description",
                LocalDate.of(2000, 01, 01), 2000);

        Integer id = film.getId();
        updatedFilm.setId(id);
        updatedFilm.setMpa(mpa);

        mpaDbStorage.addMpaToFilm(updatedFilm);
        filmStorage.update(updatedFilm);

        Film filmFromDb = filmStorage.getFilmById(1);
        mpaDbStorage.addMpaToFilm(filmFromDb);

        assertEquals(updatedFilm, filmFromDb);

    }

    @Test
    void deleteShouldDeleteFilmUponRequest() {
        Film film = new Film("Film1", "Description",
                LocalDate.of(2000, 01, 01), 2000);
        Mpa mpa = new Mpa(1, "Комедия");
        film.setMpa(mpa);

        filmStorage.add(film);
        mpaDbStorage.addMpaToFilm(film);

        Integer id = film.getId();
        filmStorage.delete(id);

        final Exception exception = assertThrows(
                ru.yandex.practicum.filmorate.exception.NotFoundException.class,
                () -> filmStorage.getFilmById(id)
        );
        assertEquals("Отсутствуют данные в БД по указанному ID", exception.getMessage());

    }

    @Test
    public void getAllFilmsShouldProvideAllFilmsUponRequest() {
        Film film = new Film("Film1", "Description",
                LocalDate.of(2000, 01, 01), 2000);
        Mpa mpa = new Mpa(1, "Комедия");
        film.setMpa(mpa);
        filmStorage.add(film);
        mpaDbStorage.addMpaToFilm(film);

        Film film2 = new Film("Film2", "Description2",
                LocalDate.of(2000, 01, 01), 2000);
        film.setMpa(mpa);
        filmStorage.add(film);
        mpaDbStorage.addMpaToFilm(film);

        Film film3 = new Film("Film3", "Description3",
                LocalDate.of(2000, 01, 01), 2000);
        film.setMpa(mpa);
        filmStorage.add(film);
        mpaDbStorage.addMpaToFilm(film);

        List<Film> allFilms = new ArrayList<>();
        allFilms.add(film);
        allFilms.add(film2);
        allFilms.add(film3);

        assertEquals(allFilms.size(), filmStorage.getAllFilms().size());
    }


    @Test
    public void getFilmByIdShouldProvideFilmByIdCorrectly() {
        Film film = new Film("Film1", "Description",
                LocalDate.of(2000, 01, 01), 2000);
        Mpa mpa = new Mpa(1, "Комедия");
        film.setMpa(mpa);
        filmStorage.add(film);
        mpaDbStorage.addMpaToFilm(film);

        List<Film> allFilms = new ArrayList<>();
        allFilms.add(film);

        Film filmFromDb = filmStorage.getFilmById(1);
        mpaDbStorage.addMpaToFilm(filmFromDb);

        assertEquals(film, filmFromDb);
    }


    @Test
    public void shouldProvideAllMPAsUponRequest() {
        List<Mpa> allMpas = new ArrayList<>();
        Mpa mpa1 = new Mpa();
        mpa1.setId(1);
        mpa1.setName("G");
        allMpas.add(mpa1);

        Mpa mpa2 = new Mpa();
        mpa2.setId(2);
        mpa2.setName("PG");
        allMpas.add(mpa2);

        Mpa mpa3 = new Mpa();
        mpa3.setId(3);
        mpa3.setName("PG-13");
        allMpas.add(mpa3);

        Mpa mpa4 = new Mpa();
        mpa4.setId(4);
        mpa4.setName("R");
        allMpas.add(mpa4);

        Mpa mpa5 = new Mpa();
        mpa5.setId(5);
        mpa5.setName("NC-17");
        allMpas.add(mpa5);

        assertEquals(allMpas.size(), filmStorage.getAllMpa().size());
        assertEquals(mpa1, mpaDbStorage.getMpaById(1));
        assertEquals(mpa2, mpaDbStorage.getMpaById(2));
        assertEquals(mpa3, mpaDbStorage.getMpaById(3));
        assertEquals(allMpas, filmStorage.getAllMpa());
    }

    @Test
    public void getAllGenresAndGetGenreByIdShouldProvideGenresCorrectly() {
        List<Genre> allGenres = new ArrayList<>();
        Genre genre1 = new Genre();
        genre1.setId(1);
        genre1.setName("Комедия");
        allGenres.add(genre1);

        Genre genre2 = new Genre();
        genre2.setId(2);
        genre2.setName("Драма");
        allGenres.add(genre2);

        Genre genre3 = new Genre();
        genre3.setId(3);
        genre3.setName("Мультфильм");
        allGenres.add(genre3);

        Genre genre4 = new Genre();
        genre4.setId(4);
        genre4.setName("Триллер");
        allGenres.add(genre4);

        Genre genre5 = new Genre();
        genre5.setId(5);
        genre5.setName("Документальный");
        allGenres.add(genre5);

        Genre genre6 = new Genre();
        genre6.setId(6);
        genre6.setName("Боевик");
        allGenres.add(genre6);

        assertEquals(allGenres.size(), filmStorage.getAllGenres().size());
        assertEquals(genre1, genreStorage.getGenreById(1));
        assertEquals(genre2, genreStorage.getGenreById(2));
        assertEquals(genre3, genreStorage.getGenreById(3));
        assertEquals(allGenres, genreStorage.getAllGenres());
    }

    @Test
    public void getTopFilmsShouldProvideTopFilms() {
        User user = new User("user@gmail.com", "user", LocalDate.of(1999, 01, 01));
        User user2 = new User("user2@gmail.com", "user2", LocalDate.of(2000, 01, 01));
        User user3 = new User("user3@gmail.com", "user3", LocalDate.of(2002, 01, 01));

        userStorage.addUser(user);
        userStorage.addUser(user2);
        userStorage.addUser(user3);

        Film film = new Film("Film1", "Description",
                LocalDate.of(2000, 01, 01), 2000);
        Mpa mpa = new Mpa(1, "Комедия");
        film.setMpa(mpa);
        filmStorage.add(film);
        mpaDbStorage.addMpaToFilm(film);

        Film film2 = new Film("Film2", "Description2",
                LocalDate.of(2000, 01, 01), 2000);
        film2.setMpa(mpa);
        filmStorage.add(film2);
        mpaDbStorage.addMpaToFilm(film2);

        Film film3 = new Film("Film3", "Description3",
                LocalDate.of(2000, 01, 01), 2000);
        film3.setMpa(mpa);
        filmStorage.add(film3);
        mpaDbStorage.addMpaToFilm(film3);

        filmStorage.putLikeToFilm(1, 1);
        filmStorage.putLikeToFilm(1, 2);
        filmStorage.putLikeToFilm(1, 3);

        filmStorage.putLikeToFilm(2, 1);
        filmStorage.putLikeToFilm(2, 2);

        filmStorage.putLikeToFilm(3, 1);

        List<Integer> topFilms = new ArrayList<>();
        topFilms.add(1);
        topFilms.add(2);
        topFilms.add(3);

        assertEquals(topFilms, filmStorage.getTopFilms(3));
    }
}
