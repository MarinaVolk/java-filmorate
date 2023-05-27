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
import ru.yandex.practicum.filmorate.storage.DbFilmStorage;
import ru.yandex.practicum.filmorate.storage.DbUserStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Qualifier
// публичные методы хранилища: User: addUser, updateUser, deleteUser, getAllUsers, getUserById
// Films: add, update, delete, getAllFilms, getFilmById, getGenreById, getAllGenres,
// getMpaById, getAllMpa,getTopFilms

class FilmorateApplicationTestsDb {
    private final DbUserStorage userStorage;
    private final DbFilmStorage filmStorage;

    @Test
    public void userCreated() {
        User user = new User("user@gmail.com", "user", LocalDate.of(1999, 01, 01));
        userStorage.addUser(user);

        assertEquals(1, userStorage.getUserById(1).getId());
        assertEquals(user, userStorage.getUserById(1));
    }

    @Test
    public void userUpdated() {
        User user = new User("user@gmail.com", "user", LocalDate.of(1999, 01, 01));
        userStorage.addUser(user);
        Integer id = user.getId();

        User userUpdated = new User("userUpdated@gmail.com", "userUpdated", LocalDate.of(1999, 01, 01));
        userUpdated.setId(id);
        userStorage.updateUser(userUpdated);

        assertEquals("userUpdated", userStorage.getUserById(1).getLogin());
    }

    @Test
    public void userDeleted() {
        User user = new User("user@gmail.com", "user", LocalDate.of(1999, 01, 01));
        userStorage.addUser(user);
        Integer id = user.getId();

        userStorage.deleteUser(id);

        final Exception exception = assertThrows(
                ru.yandex.practicum.filmorate.exception.NotFoundException.class,
                () -> userStorage.getUserById(id)
        );
        assertEquals("Отсутствуют данные в БД по указанному ID.", exception.getMessage());
    }

    @Test
    public void allUsersAreProvidedUponRequest() {
        User user = new User("user@gmail.com", "user", LocalDate.of(1999, 01, 01));
        User user2 = new User("user2@gmail.com", "user2", LocalDate.of(2000, 01, 01));
        User user3 = new User("user3@gmail.com", "user3", LocalDate.of(2002, 01, 01));

        userStorage.addUser(user);
        userStorage.addUser(user2);
        userStorage.addUser(user3);
        List<User> allUsers = new ArrayList<>();
        allUsers.add(user);
        allUsers.add(user2);
        allUsers.add(user3);

        assertEquals(allUsers.size(), userStorage.getAllUsers().size());
        assertEquals(user, userStorage.getUserById(1));
        assertEquals(user2, userStorage.getUserById(2));
        assertEquals(user3, userStorage.getUserById(3));
    }

    @Test
    void shouldAddFilm() {
        Film film = new Film("Film1", "Description",
                LocalDate.of(2000, 01, 01), 2000);
        Mpa mpa = filmStorage.getAllMpa().get(1);
        film.setMpa(mpa);
        filmStorage.add(film);

        assertEquals(1, filmStorage.getFilmById(1).getId());
        assertEquals(film, filmStorage.getFilmById(1));

    }

    @Test
    void shouldUpdateFilm() {
        Film film = new Film("Film1", "Description",
                LocalDate.of(2000, 01, 01), 2000);
        Mpa mpa = filmStorage.getAllMpa().get(1);
        film.setMpa(mpa);
        filmStorage.add(film);

        Film updatedFilm = new Film("Film1", "Updated_Description",
                LocalDate.of(2000, 01, 01), 2000);

        Integer id = film.getId();
        updatedFilm.setId(id);
        updatedFilm.setMpa(mpa);

        filmStorage.update(updatedFilm);

        assertEquals(updatedFilm, filmStorage.getFilmById(id));

    }

    @Test
    public void shouldProvideAllFilmsUponRequest() {
        Film film = new Film("Film1", "Description",
                LocalDate.of(2000, 01, 01), 2000);
        Mpa mpa1 = filmStorage.getAllMpa().get(1);
        film.setMpa(mpa1);

        Film film2 = new Film("Film2", "Description2",
                LocalDate.of(2000, 01, 01), 2000);
        Mpa mpa2 = filmStorage.getAllMpa().get(2);
        film2.setMpa(mpa2);

        Film film3 = new Film("Film3", "Description3",
                LocalDate.of(2000, 01, 01), 2000);
        Mpa mpa3 = filmStorage.getAllMpa().get(3);
        film3.setMpa(mpa3);

        filmStorage.add(film);
        filmStorage.add(film2);
        filmStorage.add(film3);
        List<Film> allFilms = new ArrayList<>();
        allFilms.add(film);
        allFilms.add(film2);
        allFilms.add(film3);

        assertEquals(allFilms.size(), filmStorage.getAllFilms().size());
        assertEquals(film, filmStorage.getFilmById(1));
        assertEquals(film2, filmStorage.getFilmById(2));
        assertEquals(film3, filmStorage.getFilmById(3));
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
        assertEquals(mpa1, filmStorage.getMpaById(1));
        assertEquals(mpa2, filmStorage.getMpaById(2));
        assertEquals(mpa3, filmStorage.getMpaById(3));
        assertEquals(allMpas, filmStorage.getAllMpa());
    }

    @Test
    public void shouldProvideAllGenresUponRequest() {
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
        assertEquals(genre1, filmStorage.getGenreById(1));
        assertEquals(genre2, filmStorage.getGenreById(2));
        assertEquals(genre3, filmStorage.getGenreById(3));
        assertEquals(allGenres, filmStorage.getAllGenres());
    }

    @Test
    public void shouldProvideTopFilms() {
        User user = new User("user@gmail.com", "user", LocalDate.of(1999, 01, 01));
        User user2 = new User("user2@gmail.com", "user2", LocalDate.of(2000, 01, 01));
        User user3 = new User("user3@gmail.com", "user3", LocalDate.of(2002, 01, 01));

        userStorage.addUser(user);
        userStorage.addUser(user2);
        userStorage.addUser(user3);

        Film film = new Film("Film1", "Description",
                LocalDate.of(2000, 01, 01), 2000);
        Mpa mpa1 = filmStorage.getAllMpa().get(1);
        film.setMpa(mpa1);
        Set<Integer> likes = new HashSet<>();
        likes.add(1);
        likes.add(2);
        likes.add(3);
        film.setLikes(likes);
        filmStorage.add(film);

        Film film2 = new Film("Film2", "Description2",
                LocalDate.of(2000, 01, 01), 2000);
        Mpa mpa2 = filmStorage.getAllMpa().get(2);
        film2.setMpa(mpa2);
        Set<Integer> likes2 = new HashSet<>();
        likes2.add(1);
        likes2.add(2);
        film2.setLikes(likes2);
        filmStorage.add(film2);

        Film film3 = new Film("Film3", "Description3",
                LocalDate.of(2000, 01, 01), 2000);
        Mpa mpa3 = filmStorage.getAllMpa().get(3);
        film3.setMpa(mpa3);
        Set<Integer> likes3 = new HashSet<>();
        likes3.add(1);
        film3.setLikes(likes3);
        filmStorage.add(film3);

        List<Integer> topFilms = new ArrayList<>();
        topFilms.add(1);
        topFilms.add(2);
        topFilms.add(3);

        assertEquals(topFilms, filmStorage.getTopFilms(3));
    }
}
