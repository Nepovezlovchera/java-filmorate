package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@AutoConfigureTestDatabase
@Import({GenreDbStorage.class, GenreRowMapper.class})
@Sql(scripts = "/schema.sql")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GenreDbStorageTest {

    private final GenreDbStorage genreDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM film_genres");
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM genre");
    }

    @Test
    void testFindAllGenres() {
        List<Genre> genres = genreDbStorage.findAllGenre();
        assertThat(genres).hasSizeGreaterThanOrEqualTo(0);
    }

    @Test
    void testFindGenreById() {
        Genre newGenre = new Genre();
        newGenre.setName("Комедия");
        Genre created = genreDbStorage.createGenre(newGenre);

        Optional<Genre> genre = genreDbStorage.findByIdGenre(created.getId());
        assertThat(genre).isPresent();
        assertThat(genre.get().getName()).isEqualTo("Комедия");
    }

    @Test
    void testCreateGenre() {
        Genre newGenre = new Genre();
        newGenre.setName("Фэнтези");

        Genre created = genreDbStorage.createGenre(newGenre);

        assertThat(created.getId()).isNotNull().isGreaterThan(0);
        assertThat(created.getName()).isEqualTo("Фэнтези");
    }

    @Test
    void testUpdateGenre() {
        Genre newGenre = new Genre();
        newGenre.setName("Для обновления");
        Genre created = genreDbStorage.createGenre(newGenre);

        created.setName("Обновлённый жанр");
        Genre updated = genreDbStorage.updateGenre(created);

        assertThat(updated.getName()).isEqualTo("Обновлённый жанр");
    }

    @Test
    void testCreateDuplicateGenre() {
        Genre genre1 = new Genre();
        genre1.setName("Уникальный жанр");
        genreDbStorage.createGenre(genre1);

        Genre genre2 = new Genre();
        genre2.setName("Уникальный жанр");

        assertThrows(Exception.class, () -> genreDbStorage.createGenre(genre2));
    }
}