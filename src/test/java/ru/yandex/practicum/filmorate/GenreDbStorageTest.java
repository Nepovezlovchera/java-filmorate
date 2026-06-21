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
    import static org.junit.jupiter.api.Assertions.assertThrows;

    @JdbcTest
    @AutoConfigureTestDatabase
    @Import({GenreDbStorage.class, GenreRowMapper.class})
    @Sql(scripts = {"/schema.sql", "/data.sql"})
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

        @Test
        void testFindGenreByIdWithInvalidId() {
            Optional<Genre> genre = genreDbStorage.findByIdGenre(-1L);
            assertThat(genre).isEmpty();
        }

        @Test
        void testFindAllGenresOrdered() {
            List<Genre> genres = genreDbStorage.findAllGenre();
            assertThat(genres).isSortedAccordingTo((g1, g2) -> g1.getId().compareTo(g2.getId()));
        }

        @Test
        void testCreateGenre() {
            Genre newGenre = new Genre();
            newGenre.setName("Фэнтези");
            Genre created = genreDbStorage.createGenre(newGenre);
            assertThat(created.getId()).isNotNull();
            assertThat(created.getName()).isEqualTo("Фэнтези");

            Optional<Genre> found = genreDbStorage.findByIdGenre(created.getId());
            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("Фэнтези");
        }

        @Test
        void testCreateDuplicateGenre() {
            // Используем уникальное имя для нового жанра
            Genre newGenre = new Genre();
            newGenre.setName("Вестерн");
            Genre created = genreDbStorage.createGenre(newGenre);
            assertThat(created.getId()).isNotNull();
            assertThat(created.getName()).isEqualTo("Вестерн");
        }

        @Test
        void testUpdateGenre() {
            // Создаем новый жанр с уникальным именем
            Genre newGenre = new Genre();
            newGenre.setName("Для обновления");
            Genre created = genreDbStorage.createGenre(newGenre);

            created.setName("Обновленное имя");
            Genre updated = genreDbStorage.updateGenre(created);

            assertThat(updated.getId()).isEqualTo(created.getId());
            assertThat(updated.getName()).isEqualTo("Обновленное имя");

            Optional<Genre> found = genreDbStorage.findByIdGenre(created.getId());
            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("Обновленное имя");
        }

        @Test
        void testUpdateGenreNotFound() {
            Genre genre = new Genre();
            genre.setId(999L);
            genre.setName("Несуществующий жанр");

            Genre updated = genreDbStorage.updateGenre(genre);
            assertThat(updated).isNotNull();
            assertThat(updated.getId()).isEqualTo(999L);
        }

        @Test
        void testCreateGenreWithNullName() {
            Genre genre = new Genre();
            genre.setName(null);

            assertThrows(Exception.class, () -> {
                genreDbStorage.createGenre(genre);
            });
        }

        @Test
        void testFindAllGenresContainsExpectedGenres() {
            List<Genre> genres = genreDbStorage.findAllGenre();
            assertThat(genres)
                    .extracting(Genre::getName)
                    .contains("Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик");
        }
    }