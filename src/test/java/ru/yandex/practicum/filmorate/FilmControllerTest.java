package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController controller;

    @BeforeEach
    void setUp() {
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        FilmStorage filmStorage = new InMemoryFilmStorage();
        FilmService filmService = new FilmService(userService, filmStorage);
        controller = new FilmController(filmService);
    }

    private Film makeFilmForTest(String name, String description, LocalDate releaseDate, int duration) {
        Film film = new Film();
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        return film;
    }

    //createFilm
    @Test
    void shouldCreateFilmSuccessfully() {
        Film film = makeFilmForTest("Название", "Описание", LocalDate.of(2000, 1, 1), 120);
        Film created = controller.createFilm(film);
        assertNotNull(created.getId());
        assertEquals("Название", created.getName());
    }

    @Test
    void shouldThrowWhenNameIsNull() {
        Film film = makeFilmForTest(null, "Описание", LocalDate.of(2000, 1, 1), 120);
        assertThrows(ConditionsNotMetException.class, () -> controller.createFilm(film));
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        Film film = makeFilmForTest("   ", "Описание", LocalDate.of(2000, 1, 1), 120);
        assertThrows(ConditionsNotMetException.class, () -> controller.createFilm(film));
    }

    @Test
    void shouldPassWhenDescriptionIs200Chars() {
        Film film = makeFilmForTest("Название", "а".repeat(200), LocalDate.of(2000, 1, 1), 120);
        assertDoesNotThrow(() -> controller.createFilm(film));
    }

    @Test
    void shouldThrowWhenDescriptionExceeds200Chars() {
        Film film = makeFilmForTest("Название", "а".repeat(201), LocalDate.of(2000, 1, 1), 120);
        assertThrows(ConditionsNotMetException.class, () -> controller.createFilm(film));
    }

    @Test
    void shouldPassWhenReleaseDateIsCinemaBirthday() {
        Film film = makeFilmForTest("Название", "Описание", LocalDate.of(1895, 12, 28), 120);
        assertDoesNotThrow(() -> controller.createFilm(film));
    }

    @Test
    void shouldThrowWhenReleaseDateIsBeforeCinemaBirthday() {
        Film film = makeFilmForTest("Название", "Описание", LocalDate.of(1895, 12, 27), 120);
        assertThrows(ConditionsNotMetException.class, () -> controller.createFilm(film));
    }

    @Test
    void shouldThrowWhenDurationIsZero() {
        Film film = makeFilmForTest("Название", "Описание", LocalDate.of(2000, 1, 1), 0);
        assertThrows(ConditionsNotMetException.class, () -> controller.createFilm(film));
    }

    @Test
    void shouldThrowWhenDurationIsNegative() {
        Film film = makeFilmForTest("Название", "Описание", LocalDate.of(2000, 1, 1), -1);
        assertThrows(ConditionsNotMetException.class, () -> controller.createFilm(film));
    }

    @Test
    void shouldPassWhenDurationIsPositive() {
        Film film = makeFilmForTest("Название", "Описание", LocalDate.of(2000, 1, 1), 1);
        assertDoesNotThrow(() -> controller.createFilm(film));
    }

    //update
    @Test
    void shouldThrowWhenUpdateWithoutId() {
        Film film = makeFilmForTest("Название", "Описание", LocalDate.of(2000, 1, 1), 120);
        assertThrows(ConditionsNotMetException.class, () -> controller.updateFilm(film));
    }

    @Test
    void shouldThrowWhenUpdateNotExistingFilm() {
        Film film = makeFilmForTest("Название", "Описание", LocalDate.of(2000, 1, 1), 120);
        film.setId(999L);
        assertThrows(NotFoundException.class, () -> controller.updateFilm(film));
    }


    @Test
    void shouldPassWhenUpdateNameIsNull() {
        Film created = controller.createFilm(makeFilmForTest("Название", "Описание", LocalDate.of(2000, 1, 1), 120));
        Film update = makeFilmForTest(null, "Описание", LocalDate.of(2000, 1, 1), 120);
        update.setId(created.getId());
        assertDoesNotThrow(() -> controller.updateFilm(update));
    }


    @Test
    void shouldThrowWhenUpdateNameIsBlank() {
        Film created = controller.createFilm(makeFilmForTest("Название", "Описание", LocalDate.of(2000, 1, 1), 120));
        Film update = makeFilmForTest("   ", "Описание", LocalDate.of(2000, 1, 1), 120);
        update.setId(created.getId());
        assertThrows(ConditionsNotMetException.class, () -> controller.updateFilm(update));
    }

    @Test
    void shouldThrowWhenUpdateDescriptionExceeds200Chars() {
        Film created = controller.createFilm(makeFilmForTest("Название", "Описание", LocalDate.of(2000, 1, 1), 120));
        Film update = makeFilmForTest("Название", "а".repeat(201), LocalDate.of(2000, 1, 1), 120);
        update.setId(created.getId());
        assertThrows(ConditionsNotMetException.class, () -> controller.updateFilm(update));
    }

    @Test
    void shouldPassWhenUpdateDescriptionIs200Chars() {
        Film created = controller.createFilm(makeFilmForTest("Название", "Описание", LocalDate.of(2000, 1, 1), 120));
        Film update = makeFilmForTest("Название", "а".repeat(200), LocalDate.of(2000, 1, 1), 120);
        update.setId(created.getId());
        assertDoesNotThrow(() -> controller.updateFilm(update));
    }

    @Test
    void shouldThrowWhenUpdateReleaseDateIsBeforeCinemaBirthday() {
        Film created = controller.createFilm(makeFilmForTest("Название", "Описание", LocalDate.of(2000, 1, 1), 120));
        Film update = makeFilmForTest("Название", "Описание", LocalDate.of(1895, 12, 27), 120);
        update.setId(created.getId());
        assertThrows(ConditionsNotMetException.class, () -> controller.updateFilm(update));
    }

    @Test
    void shouldPassWhenUpdateReleaseDateIsCinemaBirthday() {
        Film created = controller.createFilm(makeFilmForTest("Название", "Описание", LocalDate.of(2000, 1, 1), 120));
        Film update = makeFilmForTest("Название", "Описание", LocalDate.of(1895, 12, 28), 120);
        update.setId(created.getId());
        assertDoesNotThrow(() -> controller.updateFilm(update));
    }

    @Test
    void shouldThrowWhenUpdateDurationIsNegative() {
        Film created = controller.createFilm(makeFilmForTest("Название", "Описание", LocalDate.of(2000, 1, 1), 120));
        Film update = makeFilmForTest("Название", "Описание", LocalDate.of(2000, 1, 1), -1);
        update.setId(created.getId());
        assertThrows(ConditionsNotMetException.class, () -> controller.updateFilm(update));
    }

    @Test
    void shouldUpdateFilmSuccessfully() {
        Film created = controller.createFilm(makeFilmForTest("Старое", "Описание", LocalDate.of(2000, 1, 1), 120));
        Film update = makeFilmForTest("Новое", "Новое описание", LocalDate.of(2010, 5, 5), 90);
        update.setId(created.getId());
        Film result = controller.updateFilm(update);
        assertEquals("Новое", result.getName());
        assertEquals("Новое описание", result.getDescription());
        assertEquals(LocalDate.of(2010, 5, 5), result.getReleaseDate());
        assertEquals(90, result.getDuration());
    }
}