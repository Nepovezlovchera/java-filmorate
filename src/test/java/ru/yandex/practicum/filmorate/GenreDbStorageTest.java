package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({GenreDbStorage.class, GenreRowMapper.class})
@Sql(scripts = {"/schema.sql", "/data-test.sql"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbStorageTest {
    private final GenreDbStorage genreDbStorage;

    @Test
    void testFindAllGenres() {
        List<Genre> genres = genreDbStorage.findAllGenre();

        assertThat(genres).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void testFindGenreById() {
        Optional<Genre> genre = genreDbStorage.findByIdGenre(1L);

        assertThat(genre).isPresent();
        assertThat(genre.get().getName()).isEqualTo("Комедия");
    }

    @Test
    void testFindGenreByIdNotFound() {
        Optional<Genre> genre = genreDbStorage.findByIdGenre(999L);
        assertThat(genre).isEmpty();
    }
}