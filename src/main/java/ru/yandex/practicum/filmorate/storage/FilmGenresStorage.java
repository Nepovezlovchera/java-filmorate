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

    private static final String GET_GENRES_BY_FILM = "SELECT g.genre_id, g.genre_name FROM genre g " +
                    "JOIN film_genres fg ON g.genre_id = fg.genre_id " +
                    "WHERE fg.film_id = ? ORDER BY g.genre_id";

    public FilmGenresStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void addGenres(long filmId, Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) return;
        removeAllGenres(filmId);
        List<Genre> list = List.copyOf(genres);
        jdbc.batchUpdate(ADD_GENRE, list, list.size(), (ps, g) -> { ps.setLong(1, filmId); ps.setLong(2, g.getId()); ps.setInt(3, list.indexOf(g)); });
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