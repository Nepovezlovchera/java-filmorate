package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

@Repository("filmDbStorage")
public class FilmDbStorage extends BaseStorage<Film> implements FilmStorage {

    private final FilmGenresStorage filmGenresStorage;

    private static final String GET_FILMS = "SELECT f.*, m.mpa_id AS mpa_id," +
            " m.mpa_name AS mpa_name FROM films f LEFT JOIN mpa m ON f.mpa_id = m.mpa_id";

    private static final String FIND_BY_ID = "SELECT f.*, m.mpa_id AS mpa_id," +
            " m.mpa_name AS mpa_name FROM films f LEFT JOIN mpa m ON f.mpa_id = m.mpa_id WHERE f.film_id = ?";

    private static final String CREATE_FILM = "INSERT INTO films (film_name," +
            " description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_FILM = "UPDATE films SET film_name = ?," +
            " description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";

    private static final String GET_POPULAR = "SELECT f.*, m.mpa_id AS mpa_id, m.mpa_name AS mpa_name, " +
            "COUNT(l.user_id) AS likes_count FROM films f LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
            "LEFT JOIN likes l ON f.film_id = l.film_id " +
            "GROUP BY f.film_id, m.mpa_id, m.mpa_name " +
            "ORDER BY likes_count DESC, f.film_id LIMIT ?";

    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper mapper, FilmGenresStorage filmGenresStorage) {
        super(jdbc, mapper);
        this.filmGenresStorage = filmGenresStorage;
    }

    @Override
    public Film createFilm(Film film) {
        Long mpaId = film.getMpa() != null ? film.getMpa().getId() : null;

        long id = insert(CREATE_FILM, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), mpaId);

        film.setId(id);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            filmGenresStorage.addGenres(id, film.getGenres());
        }

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        Long mpaId = film.getMpa() != null ? film.getMpa().getId() : null;

        update(UPDATE_FILM, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), mpaId, film.getId());

        filmGenresStorage.removeAllGenres(film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            filmGenresStorage.addGenres(film.getId(), film.getGenres());
        }

        return findById(film.getId()).orElseThrow();
    }

    @Override
    public Collection<Film> getFilms() {
        return findMany(GET_FILMS);
    }

    @Override
    public Optional<Film> findById(long id) {
        return findOne(FIND_BY_ID, id);
    }

    public Collection<Film> getPopular(int count) {
        return findMany(GET_POPULAR, count);
    }
}