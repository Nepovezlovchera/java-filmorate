package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;


@Repository("filmDbStorage")
public class FilmDbStorage extends BaseStorage<Film> implements FilmStorage {

    private final FilmGenresStorage filmGenresStorage;

    private static final String GET_FILMS = "SELECT f.*, m.mpa_id AS mpa_id, m.mpa_name AS mpa_name " +
            "FROM films f " +
            "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id";

    private static final String FIND_BY_ID = " SELECT f.*,m.mpa_id AS mpa_id, m.mpa_name AS mpa_name " +
            "FROM films f LEFT JOIN mpa m ON f.mpa_id = m.mpa_id WHERE f.film_id = ?";

    private static final String CREATE_FILM = "INSERT INTO films (film_name, description, release_date, duration, mpa_id) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_FILM = "UPDATE films SET film_name = ?, description = ?, release_date = ?, " +
            "duration = ?, mpa_id = ? WHERE film_id = ?";

    private static final String GET_GENRES_FOR_FILM = " SELECT g.genre_id, g.genre_name " +
            "FROM genre g JOIN film_genres fg ON g.genre_id = fg.genre_id " +
            "WHERE fg.film_id = ? ORDER BY g.genre_id";

    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper mapper, FilmGenresStorage filmGenresStorage) {
        super(jdbc, mapper);
        this.filmGenresStorage = filmGenresStorage;
    }

    @Override
    public Film createFilm(Film film) {
        Long mpaId = film.getMpa() != null ? film.getMpa().getId() : null;

        long id = insert(CREATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                mpaId
        );

        film.setId(id);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            filmGenresStorage.addGenres(id, film.getGenres());
        }

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        update(UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId());
        filmGenresStorage.removeAllGenres(film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            filmGenresStorage.addGenres(film.getId(), film.getGenres());
        }
        return findById(film.getId()).orElseThrow();
    }

    @Override
    public Collection<Film> getFilms() {
        List<Film> films = findMany(GET_FILMS);
        films.forEach(film -> {
            List<Genre> genres = jdbc.query(GET_GENRES_FOR_FILM,
                    (rs, rowNum) -> {
                        Genre genre = new Genre();
                        genre.setId(rs.getLong("genre_id"));
                        genre.setName(rs.getString("genre_name"));
                        return genre;
                    },
                    film.getId());
            film.setGenres(new LinkedHashSet<>(genres));
        });
        return films;
    }

    @Override
    public Optional<Film> findById(long id) {
        Optional<Film> filmOptional = findOne(FIND_BY_ID, id);

        if (filmOptional.isPresent()) {
            Film film = filmOptional.get();

            List<Genre> genres = jdbc.query(GET_GENRES_FOR_FILM,
                    (rs, rowNum) -> {
                        Genre genre = new Genre();
                        genre.setId(rs.getLong("genre_id"));
                        genre.setName(rs.getString("genre_name"));
                        return genre;
                    },
                    id);

            film.setGenres(new HashSet<>(genres));
        }

        return filmOptional;
    }
}