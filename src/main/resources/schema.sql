DROP TABLE IF EXISTS FILMS, GENRES, GENRESLIST, LIKESLIST, MPA, USERS, USER_FRIENDSHIP;

CREATE TABLE IF NOT EXISTS mpa (
    rating_id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name_mpa VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS genres (
    genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
    film_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200) NOT NULL,
    releaseDate DATE,
    duration INT CHECK (duration > 0),
    rating_id INT REFERENCES mpa (rating_id)
);

CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR(50) NOT NULL,
    login VARCHAR(50) NOT NULL,
    name VARCHAR(50) NOT NULL,
    birthday DATE,
    UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS genresList (
    film_id INTEGER REFERENCES films (film_id),
    genre_id INTEGER REFERENCES genres (genre_id)
);

CREATE TABLE IF NOT EXISTS likesList (
    film_id INTEGER REFERENCES films (film_id),
    user_id INTEGER REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS user_friendship (
    user_id INTEGER REFERENCES users (user_id),
    user2_id INTEGER REFERENCES users (user_id)
);
