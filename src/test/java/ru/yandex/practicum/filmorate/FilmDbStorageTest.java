package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmGenresStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, FilmRowMapper.class, FilmGenresStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private Long testFilmId;

    @BeforeEach
    void setUp() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1L);
        film.setMpa(mpa);

        Film created = filmStorage.createFilm(film);
        testFilmId = created.getId();
    }

    @Test
    void testCreateFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1L);
        film.setMpa(mpa);

        Film created = filmStorage.createFilm(film);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Test Film");
        assertThat(created.getDescription()).isEqualTo("Test Description");
        assertThat(created.getReleaseDate()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(created.getDuration()).isEqualTo(120);
    }

    @Test
    void testFindFilmById() {
        Optional<Film> found = filmStorage.findById(testFilmId);

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Film");
        assertThat(found.get().getDescription()).isEqualTo("Test Description");
        assertThat(found.get().getReleaseDate()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(found.get().getDuration()).isEqualTo(120);
    }

    @Test
    void testFindFilmByIdNotFound() {
        Optional<Film> found = filmStorage.findById(999L);
        assertThat(found).isEmpty();
    }

    @Test
    void testUpdateFilm() {
        Optional<Film> filmOpt = filmStorage.findById(testFilmId);
        assertThat(filmOpt).isPresent();

        Film film = filmOpt.get();
        film.setName("New Film");
        film.setDescription("New Description");
        film.setReleaseDate(LocalDate.of(2021, 2, 2));
        film.setDuration(150);

        Film updated = filmStorage.updateFilm(film);

        assertThat(updated.getName()).isEqualTo("New Film");
        assertThat(updated.getDescription()).isEqualTo("New Description");
        assertThat(updated.getReleaseDate()).isEqualTo(LocalDate.of(2021, 2, 2));
        assertThat(updated.getDuration()).isEqualTo(150);
    }

    @Test
    void testGetFilms() {
        // В setUp уже создан один фильм, создаем еще один
        Film film2 = new Film();
        film2.setName("Film Two");
        film2.setDescription("Description Two");
        film2.setReleaseDate(LocalDate.of(2021, 2, 2));
        film2.setDuration(150);
        Mpa mpa = new Mpa();
        mpa.setId(2L);
        film2.setMpa(mpa);
        filmStorage.createFilm(film2);

        Collection<Film> films = filmStorage.getFilms();

        assertThat(films).hasSizeGreaterThanOrEqualTo(2);
    }
}