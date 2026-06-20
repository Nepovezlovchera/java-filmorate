package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmGenresStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmGenresStorage.class, FilmDbStorage.class, FilmRowMapper.class,
        GenreRowMapper.class, GenreDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmGenresStorageTest {
    private final FilmGenresStorage filmGenresStorage;
    private final FilmDbStorage filmDbStorage;
    private final GenreDbStorage genreDbStorage;
    private Long filmId;

    @BeforeEach
    void setUp() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1L);
        film.setMpa(mpa);
        filmId = filmDbStorage.createFilm(film).getId();
    }

    @Test
    void testAddGenres() {
        Set<Genre> genres = new HashSet<>();
        Genre genre1 = genreDbStorage.findByIdGenre(1L).get();
        Genre genre2 = genreDbStorage.findByIdGenre(2L).get();
        genres.add(genre1);
        genres.add(genre2);

        filmGenresStorage.addGenres(filmId, genres);

        List<Genre> savedGenres = filmGenresStorage.getGenresByFilmId(filmId);

        assertThat(savedGenres)
                .isNotNull()
                .hasSize(2)
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(1L, 2L);

        assertThat(savedGenres)
                .extracting(Genre::getName)
                .containsExactlyInAnyOrder("Комедия", "Драма");
    }

    @Test
    void testAddGenresEmptySet() {
        Set<Genre> emptyGenres = new HashSet<>();

        filmGenresStorage.addGenres(filmId, emptyGenres);

        List<Genre> savedGenres = filmGenresStorage.getGenresByFilmId(filmId);
        assertThat(savedGenres).isEmpty();
    }

    @Test
    void testAddGenresDuplicate() {
        Set<Genre> genres = new HashSet<>();
        Genre genre1 = genreDbStorage.findByIdGenre(1L).get();
        genres.add(genre1);
        filmGenresStorage.addGenres(filmId, genres);

        Set<Genre> duplicateGenres = new HashSet<>();
        duplicateGenres.add(genre1);
        filmGenresStorage.addGenres(filmId, duplicateGenres);

        List<Genre> savedGenres = filmGenresStorage.getGenresByFilmId(filmId);
        assertThat(savedGenres)
                .hasSize(1)
                .extracting(Genre::getId)
                .containsExactly(1L);
    }

    @Test
    void testRemoveAllGenres() {
        Set<Genre> genres = new HashSet<>();
        Genre genre1 = genreDbStorage.findByIdGenre(1L).get();
        Genre genre2 = genreDbStorage.findByIdGenre(2L).get();
        genres.add(genre1);
        genres.add(genre2);
        filmGenresStorage.addGenres(filmId, genres);

        List<Genre> beforeRemove = filmGenresStorage.getGenresByFilmId(filmId);
        assertThat(beforeRemove).hasSize(2);

        filmGenresStorage.removeAllGenres(filmId);

        List<Genre> afterRemove = filmGenresStorage.getGenresByFilmId(filmId);
        assertThat(afterRemove).isEmpty();
    }

    @Test
    void testRemoveAllGenresWhenEmpty() {
        filmGenresStorage.removeAllGenres(filmId);

        List<Genre> savedGenres = filmGenresStorage.getGenresByFilmId(filmId);
        assertThat(savedGenres).isEmpty();
    }

    @Test
    void testGetGenresByFilmId() {
        Set<Genre> genres = new HashSet<>();
        Genre genre1 = genreDbStorage.findByIdGenre(1L).get();
        Genre genre2 = genreDbStorage.findByIdGenre(3L).get();
        genres.add(genre1);
        genres.add(genre2);
        filmGenresStorage.addGenres(filmId, genres);

        List<Genre> savedGenres = filmGenresStorage.getGenresByFilmId(filmId);

        assertThat(savedGenres)
                .isNotNull()
                .hasSize(2)
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(1L, 3L);
    }

    @Test
    void testGetGenresByFilmIdNotFound() {
        List<Genre> genres = filmGenresStorage.getGenresByFilmId(999L);

        assertThat(genres).isEmpty();
    }

    @Test
    void testAddGenresMultipleFilms() {
        Film film2 = new Film();
        film2.setName("Second Film");
        film2.setDescription("Second Description");
        film2.setReleaseDate(LocalDate.of(2021, 2, 2));
        film2.setDuration(150);
        Mpa mpa = new Mpa();
        mpa.setId(2L);
        film2.setMpa(mpa);
        Long filmId2 = filmDbStorage.createFilm(film2).getId();

        Set<Genre> genres1 = new HashSet<>();
        genres1.add(genreDbStorage.findByIdGenre(1L).get());
        filmGenresStorage.addGenres(filmId, genres1);

        Set<Genre> genres2 = new HashSet<>();
        genres2.add(genreDbStorage.findByIdGenre(2L).get());
        genres2.add(genreDbStorage.findByIdGenre(3L).get());
        filmGenresStorage.addGenres(filmId2, genres2);

        List<Genre> savedGenres1 = filmGenresStorage.getGenresByFilmId(filmId);
        assertThat(savedGenres1)
                .hasSize(1)
                .extracting(Genre::getId)
                .containsExactly(1L);

        List<Genre> savedGenres2 = filmGenresStorage.getGenresByFilmId(filmId2);
        assertThat(savedGenres2)
                .hasSize(2)
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(2L, 3L);
    }
}