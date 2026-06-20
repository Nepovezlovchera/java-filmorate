package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
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
        Film film = new Film();
        film.setName("Find Film");
        film.setDescription("Find Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1L);
        film.setMpa(mpa);
        Film created = filmStorage.createFilm(film);

        Optional<Film> found = filmStorage.findById(created.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Find Film");
    }

    @Test
    void testFindFilmByIdNotFound() {
        Optional<Film> found = filmStorage.findById(999L);
        assertThat(found).isEmpty();
    }

    @Test
    void testUpdateFilm() {
        Film film = new Film();
        film.setName("Old Film");
        film.setDescription("Old Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1L);
        film.setMpa(mpa);
        Film created = filmStorage.createFilm(film);

        created.setName("New Film");
        created.setDescription("New Description");
        created.setReleaseDate(LocalDate.of(2021, 2, 2));
        created.setDuration(150);

        Film updated = filmStorage.updateFilm(created);

        assertThat(updated.getName()).isEqualTo("New Film");
        assertThat(updated.getDescription()).isEqualTo("New Description");
        assertThat(updated.getReleaseDate()).isEqualTo(LocalDate.of(2021, 2, 2));
        assertThat(updated.getDuration()).isEqualTo(150);
    }

    @Test
    void testGetFilms() {
        Film film1 = new Film();
        film1.setName("Film One");
        film1.setDescription("Description One");
        film1.setReleaseDate(LocalDate.of(2020, 1, 1));
        film1.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1L);
        film1.setMpa(mpa);
        filmStorage.createFilm(film1);

        Film film2 = new Film();
        film2.setName("Film Two");
        film2.setDescription("Description Two");
        film2.setReleaseDate(LocalDate.of(2021, 2, 2));
        film2.setDuration(150);
        mpa = new Mpa();
        mpa.setId(2L);
        film2.setMpa(mpa);
        filmStorage.createFilm(film2);

        Collection<Film> films = filmStorage.getFilms();

        assertThat(films).hasSizeGreaterThanOrEqualTo(2);
    }
}