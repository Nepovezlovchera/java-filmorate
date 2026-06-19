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
import java.util.Set;

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
    }

    @Test
    void testRemoveAllGenres() {
        Set<Genre> genres = new HashSet<>();
        Genre genre1 = genreDbStorage.findByIdGenre(1L).get();
        genres.add(genre1);
        filmGenresStorage.addGenres(filmId, genres);

        filmGenresStorage.removeAllGenres(filmId);
    }
}