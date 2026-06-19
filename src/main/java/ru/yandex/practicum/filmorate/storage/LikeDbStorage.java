package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.LikeRowMapper;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;

@Repository
public class LikeDbStorage extends BaseStorage<Like> {

    private final FilmDbStorage filmDbStorage;

    private static final String CREATE_LIKE = "INSERT INTO likes (user_id, film_id) VALUES (?, ?)";
    private static final String FIND_BY_FILM_ID = "SELECT * FROM likes WHERE film_id = ?";
    private static final String COUNT_LIKES = "SELECT COUNT(*) FROM likes WHERE film_id = ?";

    public LikeDbStorage(JdbcTemplate jdbc, LikeRowMapper mapper, FilmDbStorage filmDbStorage) {
        super(jdbc, mapper);
        this.filmDbStorage = filmDbStorage;
    }

    public Long create(Long userId, Long filmId) {
        List<Like> likesForFilm = findByFilmId(filmId);
        boolean likeExists = likesForFilm.stream()
                .anyMatch(like -> like.getUserId().equals(userId));

        if (likeExists) {
            return countLikes(filmId);
        }

        insert(CREATE_LIKE, userId, filmId);
        return countLikes(filmId);
    }

    public List<Like> findByFilmId(Long filmId) {
        return findMany(FIND_BY_FILM_ID, filmId);
    }

    public Long countLikes(Long filmId) {
        return count(COUNT_LIKES, filmId);
    }
}
