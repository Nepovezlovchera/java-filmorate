package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

@Repository
public class FilmGenresStorage {

    private final JdbcTemplate jdbc;

    private static final String ADD_GENRE = "INSERT INTO film_genres (film_id, genre_id, genre_order) " +
            "VALUES (?, ?, ?)";

    private static final String REMOVE_ALL_GENRES = "DELETE FROM film_genres WHERE film_id = ?";

    private static final String GET_GENRES_BY_FILM = "SELECT g.genre_id, g.genre_name " +
            "FROM genre g JOIN film_genres fg ON g.genre_id = fg.genre_id " +
            "WHERE fg.film_id = ? ORDER BY fg.genre_order";

    public FilmGenresStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void addGenres(long filmId, Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) return;

        removeAllGenres(filmId);

        int order = 0;
        for (Genre genre : genres) {
            jdbc.update(ADD_GENRE, filmId, genre.getId(), order++);
        }
    }

    public void removeAllGenres(long filmId) {
        jdbc.update(REMOVE_ALL_GENRES, filmId);
    }

    public List<Genre> getGenresByFilmId(long filmId) {
        return jdbc.query(GET_GENRES_BY_FILM,
                (rs, rowNum) -> {
                    Genre genre = new Genre();
                    genre.setId(rs.getLong("genre_id"));
                    genre.setName(rs.getString("genre_name"));
                    return genre;
                },
                filmId);
    }
}