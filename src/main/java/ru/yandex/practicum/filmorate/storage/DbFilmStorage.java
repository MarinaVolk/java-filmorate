package ru.yandex.practicum.filmorate.storage;/* # parse("File Header.java")*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.*;

/**
 * File Name: DbFilmStorage.java
 * Author: Marina Volkova
 * Date: 2023-05-26,   3:51 PM (UTC+3)
 * Description:
 */
@Component
@Qualifier("DbFilmStorage")
public class DbFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmValidator validator = new FilmValidator();

    @Autowired
    public DbFilmStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film add(Film film) {
        validator.isValid(film);

        String insertSql = "INSERT INTO FILMS (NAME, DESCRIPTION, RELEASEDATE, DURATION, RATING_ID) VALUES (?, ?, ?, ?, ?)";

        String selectSql = "SELECT film_id FROM FILMS WHERE NAME = ? AND DESCRIPTION = ? " +
                "AND RELEASEDATE = ? AND DURATION = ?";

        jdbcTemplate.update(insertSql, film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());

        SqlRowSet rs = jdbcTemplate.queryForRowSet(selectSql,
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());

        int id = 0;

        if (rs.next()) {
            id = rs.getInt("film_id");
        }
        film.setId(id);
        return film;
    }


    @Override
    public Film update(Film film) {

        if (contains(film.getId())) {

            //delete(film.getId());

            String sql = "UPDATE films SET name = ?, description = ?, releaseDate = ?, duration = ?, rating_id = ? WHERE film_id = ?";

            jdbcTemplate.update(sql, film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());

            //List<Genre> genres = film.getGenres();
            saveGenresListByFilm(film);
            //addGenresToFilm(film.getGenres(), film);

        } else {
            throw new NotFoundException("Такого фильма не существует.");
        }

        return film;
    }

    @Override
    public void delete(Integer id) {
        String sql1 = "DELETE FROM FILMS WHERE film_id = ?";
        jdbcTemplate.update(sql1, id);

        String sql2 = "DELETE FROM GENRESLIST WHERE film_id = ?";
        jdbcTemplate.update(sql2, id);

        String sql3 = "DELETE FROM LIKESLIST WHERE film_id = ?";
        jdbcTemplate.update(sql3, id);
    }


    private Map<Integer, Film> filmProcessor(SqlRowSet rowSet) {
        List<Integer> filmIdList = new ArrayList<>();
        Map<Integer, Film> films = new HashMap<>();

        // получаем список фильмов
        boolean filmFound = false;
        while (rowSet.next()) {
            filmFound = true;
            Film film = new Film(
                    rowSet.getString("name"),
                    rowSet.getString("description"),
                    rowSet.getDate("releaseDate").toLocalDate(),
                    rowSet.getInt("duration"));
            film.setId(rowSet.getInt("film_id"));

            films.put(film.getId(), film);
            // отдельно накапливаем список фильмов для запросов
            filmIdList.add(film.getId());
        }

        if (filmFound) {

            // получаем для списка фильмов список жанров
            Map<Integer, List<Genre>> filmGenres = getGenresSetIdBySeveralFilmIds(filmIdList);
            // получаем для списка фильмов список лайков
            Map<Integer, Set<Integer>> filmLikes = getLikesSetBySeveralFilmIds(filmIdList);
            // получаем для списка фильмов список mpa
            Map<Integer, List<Mpa>> filmMpas = getMpasSetBySeveralFilmIds(filmIdList);


            // цикл по фильмам - дозаполняем поля...
            for (Map.Entry<Integer, Film> entry : films.entrySet()) {
                Film film = entry.getValue();

                if (filmGenres.size() > 0) {
                    film.setGenres(filmGenres.get(film.getId()));
                } else {
                    film.setGenres(new ArrayList<>());
                }
                if (filmLikes.size() > 0) {
                    film.setLikes(filmLikes.get(film.getId()));
                } else {
                    film.setLikes(new HashSet<>());
                }
                film.setMpa(filmMpas.get(film.getId()).get(0));
            }
        }

        return films;
    }


    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM FILMS";
        // запрос к БД
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);

        Map<Integer, Film> films = filmProcessor(rowSet);

        // из Map -> List
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(Integer id) {
        String sql = "SELECT * FROM FILMS WHERE film_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        Map<Integer, Film> films = filmProcessor(rowSet);
        if (films.size() == 0) {
            throw new NotFoundException("Отсутствуют данные в БД по указанному ID");
        }
        return films.get(id);
    }

    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa";
        // 1 запрос к БД
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);

        List<Mpa> mpas = new ArrayList<>();
        try {
            while (rowSet.next()) {
                Mpa mpa = new Mpa(
                        rowSet.getInt("rating_id"),
                        rowSet.getString("name"));
                mpas.add(mpa);
            }
        } catch (NotFoundException e) {
            System.out.println("Отсутствуют MPAs в БД.");
        }
        return mpas;
    }

    public Mpa getMpaById(Integer id) {
        String sql = "SELECT * FROM MPA WHERE rating_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        Mpa mpa;
        if (rowSet.next()) {
            mpa = new Mpa(
                    rowSet.getInt("rating_id"),
                    rowSet.getString("name"));
        } else {
            throw new NotFoundException("Отсутствуют данные в БД по указанному ID.");
        }
        return mpa;
    }


    public void setMpaById(Integer id, Film film) {
        film.setMpa(getMpaById(id));
        update(film);
    }


    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM GENRES";
        // запрос к БД
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);

        List<Genre> genres = new ArrayList<>();
        try {
            while (rowSet.next()) {
                Genre genre = new Genre(
                        rowSet.getInt("genre_id"),
                        rowSet.getString("name"));
                genres.add(genre);
            }
        } catch (NotFoundException e) {
            System.out.println("Отсутствуют жанры в БД.");
        }
        return genres;
    }


    public Genre getGenreById(Integer id) {
        String sql = "SELECT * FROM GENRES WHERE genre_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        Genre genre;
        if (rowSet.next()) {
            genre = new Genre(
                    rowSet.getInt("genre_id"),
                    rowSet.getString("name"));
        } else {
            throw new NotFoundException("Отсутствуют данные в БД по указанному ID.");
        }
        return genre;
    }


    /* public Set<Integer> getGenresSetIdByFilmId(Integer id) {
        String sql = "SELECT genre_id FROM GENRESLIST WHERE film_id = ?";
        List<Integer> genres = jdbcTemplate.query(sql, ((rs, rowNum) -> rs.getInt("genre_id")), id);
        return new HashSet<>(genres);
    }  */

    /**
     * Получаем все жанры для каждого фильма.
     *
     * @param filmIds список фильмов
     * @return Мап film_id: [Genre]
     */
    public Map<Integer, List<Genre>> getGenresSetIdBySeveralFilmIds(List<Integer> filmIds) {
        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        String sql = String.format("" +
                "SELECT t.film_id, t.genre_id, r.name " +
                "FROM GENRESLIST as t " +
                "LEFT JOIN GENRES as r " +
                "ON t.genre_id = r.genre_id " +
                "WHERE t.film_id IN (%s)", inSql);

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, filmIds.toArray());
        Map<Integer, Genre> uniqueGenres = new HashMap<>();
        Map<Integer, List<Genre>> result = new HashMap<>();

        try {
            while (rowSet.next()) {

                Integer filmId = rowSet.getInt("film_id");
                Integer genreId = rowSet.getInt("genre_id");
                String genreName = rowSet.getString("name");

                // заполняем список уникальных жанров
                // чтобы не плодить экземпляры классов с одинаковым содержанием
                if (!uniqueGenres.containsKey(genreId)) {
                    uniqueGenres.put(genreId, new Genre(genreId, genreName));
                }

                // для каждого нового фильма делаем заглушку - пустой список жанров
                if (!result.containsKey(filmId)) {
                    List<Genre> genreList = new ArrayList<>();
                    result.put(filmId, genreList);
                }

                // добавляем жанр к фильму
                result.get(filmId).add(uniqueGenres.get(genreId));
            }
        } catch (NotFoundException e) {
            System.out.println("Отсутствуют жанры у этого фильма.");
        }

        return result;
    }

    // получаем все лайки для каждого фильма
    public Map<Integer, Set<Integer>> getLikesSetBySeveralFilmIds(List<Integer> filmIds) {
        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        String sql = String.format("" +
                "SELECT " +
                "t.film_id, t.user_id " +
                "FROM LIKESLIST as t " +
                "WHERE t.film_id IN (%s)", inSql);

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, filmIds.toArray());
        Map<Integer, Set<Integer>> result = new HashMap<>();

        try {
            while (rowSet.next()) {

                Integer filmId = rowSet.getInt("film_id");
                Integer userId = rowSet.getInt("user_id");

                // для каждого нового фильма делаем заглушку - пустое множество лайков
                if (!result.containsKey(filmId)) {
                    Set<Integer> likesList = new HashSet<>();
                    result.put(filmId, likesList);
                }

                // добавляем лайки к фильму
                result.get(filmId).add(userId);
            }
        } catch (NotFoundException e) {
            System.out.println("Отсутствуют лайки у этого фильма.");
        }

        return result;
    }


    // получаем все MPA для каждого фильма
    public Map<Integer, List<Mpa>> getMpasSetBySeveralFilmIds(List<Integer> filmIds) {
        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        String sql = String.format("" +
                "SELECT " +
                "t.film_id, r.rating_id, r.name " +
                "FROM FILMS as t " +
                "LEFT JOIN MPA as r " +
                "ON t.rating_id = r.rating_id " +
                "WHERE t.film_id IN (%s)", inSql);

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, filmIds.toArray());
        Map<Integer, Mpa> uniqueMPAs = new HashMap<>();
        Map<Integer, List<Mpa>> result = new HashMap<>();

        try {
            while (rowSet.next()) {

                Integer filmId = rowSet.getInt("film_id");
                Integer mpaId = rowSet.getInt("rating_id");
                String mpaName = rowSet.getString("name");

                // заполняем список уникальных жанров
                // чтобы не плодить экземпляры классов с одинаковым содержанием
                if (!uniqueMPAs.containsKey(mpaId)) {
                    uniqueMPAs.put(mpaId, new Mpa(mpaId, mpaName));
                }

                // для каждого нового фильма делаем заглушку - пустой список MPA
                if (!result.containsKey(filmId)) {
                    List<Mpa> mpaList = new ArrayList<>();
                    result.put(filmId, mpaList);
                }

                // добавляем MPA к фильму
                result.get(filmId).add(uniqueMPAs.get(mpaId));
            }
        } catch (NotFoundException e) {
            System.out.println("Отсутствуют MPA у этого фильма.");
        }
        return result;
    }


    /*private Film addGenresToFilm(Integer id, Film film) {
        // добавление жанров фильму
        Set<Integer> genres = getGenresSetIdByFilmId(id);
        List<Integer> genresThatAreSetToFilm = new ArrayList<>();
        genresThatAreSetToFilm.addAll(genres);
        List<Genre> filmsGenres = new ArrayList<>();

        for (Integer intG: genresThatAreSetToFilm) {
            Genre genre = getGenreById(intG);
            filmsGenres.add(genre);
        }
        film.setGenres(filmsGenres);
        return film;
    }  */

    public void deleteFromGenresList(Integer id) {
        String sql = "DELETE FROM GENRESLIST WHERE film_id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void setGenreById(Integer id, Film film) {
        Genre genre = getGenreById(id);
        List<Genre> filmsGenres = film.getGenres();
        filmsGenres.add(genre);
        update(film);
    }

    public void saveGenresListByFilm(Film film) {
        String sql = "INSERT INTO GENRESLIST (film_id, genre_id) VALUES (" + film.getId() + ", ?)";
        List<Genre> genres = new ArrayList<>();
        genres = film.getGenres();

        if (genres != null) {
            Set<Genre> buffer = new HashSet<>(genres);
            genres = new ArrayList<>(buffer);
            genres.sort(Comparator.comparingInt(Genre::getId));
            film.setGenres(genres);

            for (Genre genre : genres) {
                jdbcTemplate.update(sql, genre.getId());
            }
        }
        if (genres.isEmpty()) {
            String sql2 = "DELETE FROM GENRESLIST WHERE film_id = ?";
            jdbcTemplate.update(sql2, film.getId());
        }

    }


    public List<Integer> getTopFilms(int count) {
        String sql = "SELECT film_id FROM LIKESLIST GROUP BY film_id ORDER BY COUNT(user_id) DESC LIMIT ?";
        return jdbcTemplate.queryForList(sql, Integer.class, count);
    }

    public List<Integer> getMostPopular() {
        String sql = "SELECT film_id FROM LIKESLIST GROUP BY film_id ORDER BY COUNT(user_id) DESC LIMIT 1";
        return jdbcTemplate.queryForList(sql, Integer.class);
    }

    public void putLikeToFilm(Integer filmId, Integer userId) {
        if (userId < 1 || filmId < 1) {
            throw new NotFoundException("ID не может быть отрицательным.");
        }

        String sql = "INSERT INTO LIKESLIST (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);

    }

    public void dislikeFilm(Integer userId, Integer filmId) {
        String sql = "DELETE FROM LIKESLIST WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    // возвращает список пользователей
    public Set<Integer> getAllLikesByFilmId(Integer filmId) {
        String sql = "SELECT user_id FROM LIKESLIST WHERE film_id = ?";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, filmId);
        Set<Integer> usersIds = new HashSet<>();
        try {
            while (rowSet.next()) {
                Integer newInt = rowSet.getInt("user_id");
                usersIds.add(newInt);
            }
        } catch (NotFoundException e) {
            System.out.println("Отсутствуют лайки к этому фильму.");
        }
        return usersIds;
    }

    // возвращает список фильмов
    public Set<Integer> getAllLikesByUserId(Integer userId) {
        String sql = "SELECT film_id FROM LIKESLIST WHERE user_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);
        Set<Integer> filmsIds = new HashSet<>();
        try {
            while (rowSet.next()) {
                Integer newInt = rowSet.getInt("film_id");
                filmsIds.add(newInt);
            }
        } catch (NotFoundException e) {
            System.out.println("Пользователь не поставил лайки ни одному фильму.");
        }
        return filmsIds;
    }

    private boolean contains(Integer id) {
        String sql = "SELECT * FROM FILMS WHERE film_id = ?";
        return jdbcTemplate.queryForRowSet(sql, id).next();
    }

}


