package ru.yandex.practicum.filmorate.service;/* # parse("File Header.java")*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyLikedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * File Name: FilmService.java
 * Author: Marina Volkova
 * Date: 2023-05-12,   11:10 PM (UTC+3)
 * Description:
 * добавление и удаление лайка, вывод 10 наиболее популярных фильмов по количеству лайков.
 * Пусть пока каждый пользователь может поставить лайк фильму только один раз
 */

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    //private final UserService userService;
    private final FilmValidator filmValidator;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        //this.userService = userService;
        this.filmStorage = filmStorage;
        filmValidator = new FilmValidator();
    }

    public void addLike(Integer filmId, Integer userId) {
        if (filmStorage.getFilmById(filmId) == null) {
            throw new NotFoundException("Такого фильма не существует.");
        }
        Film film = filmStorage.getFilmById(filmId);

        Set<Integer> likes = film.getLikes();

        if (likes.contains(userId)) {
            throw new AlreadyLikedException("Пользователь уже поставил лайк этому фильму.");
        }
        likes.add(userId);
        filmStorage.update(film);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        if (filmStorage.getFilmById(filmId) == null) {
            throw new NotFoundException("Такого фильма не существует.");
        }
        Film film = filmStorage.getFilmById(filmId);

        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException("Этот пользователь не ставил лайк этому фильму.");
        }
        film.getLikes().remove(userId);
        filmStorage.update(film);
    }

    public List<Film> getTopFilms() {
        List<Film> allFilms = filmStorage.getAllFilms();
        //filmStorage.getAllFilms();
        //if (user == null) {
           // throw new NotFoundException("Такого пользователя не существует.");
        //}
        /* for (int i = 0; i < allFilms.size(); i++) {
            if (allFilms.get(i).getLikes().size() < allFilms.get(i+1).getLikes().size()) {
                allFilms.set(i+1, allFilms.get(i));
            }
        }   */
        Collections.sort(allFilms, new Comparator<Film>() {
            @Override
            public int compare(Film o1, Film o2) {
                return o1.getLikes().size() - o2.getLikes().size();
            }
        });
        List<Film> top10Films = allFilms
                .stream()
                .limit(10)
                .collect(Collectors.toList());
        return top10Films;
    }

    /* сортировщик
    public static void main(String[] args) {
        List<Film> allFilms = new ArrayList<>();
        Film film1 = new Film("1", "1", LocalDate.of(2000, 12, 01), 1);
        Set<Integer> set1 = new HashSet<>();
        set1.add(61);
        set1.add(6);
        film1.setLikes(set1);
        allFilms.add(film1);

        Film film2 = new Film("2", "2", LocalDate.of(2002, 12, 01), 1);
        Set<Integer> set2 = new HashSet<>();
        set2.add(7);
        film2.setLikes(set2);
        allFilms.add(film2);

        Film film3 = new Film("3", "3", LocalDate.of(2003, 12, 01), 1);
        Set<Integer> set3 = new HashSet<>();
        set3.add(5);
        set3.add(7);
        set3.add(8);
        film3.setLikes(set3);
        allFilms.add(film3);

        Film film4 = new Film("4", "4", LocalDate.of(2004, 12, 01), 1);
        Set<Integer> set4 = new HashSet<>();
        film4.setLikes(set4);
        allFilms.add(film4);

        Collections.sort(allFilms, new Comparator<Film>() {
            @Override
            public int compare(Film o1, Film o2) {
                return o2.getLikes().size() - o1.getLikes().size();
            }
        });

        System.out.println(allFilms);
    }   */


}
