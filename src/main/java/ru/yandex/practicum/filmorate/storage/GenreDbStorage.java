package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreDbStorage extends BaseStorage<Genre> {
    private static final String FIND_ALL = "SELECT * FROM genre ORDER BY genre_id";
    private static final String CREATE_GENRE = "INSERT INTO genre(genre_name) VALUES(?)";
    private static final String UPDATE_GENRE = "UPDATE genre SET genre_name = ? WHERE genre_id = ?";
    private static final String FIND_BY_ID = "SELECT * FROM genre WHERE genre_id = ?";
    private static final String FIND_ALL_BY_IDS_TEMPLATE = "SELECT * FROM genre WHERE genre_id IN (%s) ORDER BY genre_id";

    public GenreDbStorage(JdbcTemplate jdbc, GenreRowMapper mapper) {
        super(jdbc, mapper);
    }

    public List<Genre> findAllGenre() {
        return findMany(FIND_ALL);
    }

    public Genre createGenre(Genre genre) throws InternalServerException {
        Long id = insert(CREATE_GENRE, genre.getName());
        genre.setId(id);
        return genre;
    }

    public Genre updateGenre(Genre newGenre) {
        update(UPDATE_GENRE, newGenre.getName(), newGenre.getId());
        return newGenre;
    }

    public Optional<Genre> findByIdGenre(Long id) {
        return findOne(FIND_BY_ID, id);
    }
}