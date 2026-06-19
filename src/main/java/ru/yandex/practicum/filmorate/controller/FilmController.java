package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;
    private MpaDbStorage mpaDbStorage;
    private GenreDbStorage genreDbStorage;

    @Autowired
    public FilmController(FilmService filmService, MpaDbStorage mpaDbStorage, GenreDbStorage genreDbStorage) {
        this.filmService = filmService;
        this.mpaDbStorage = this.mpaDbStorage;
        this.genreDbStorage = this.genreDbStorage;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable long id) {
        return filmService.findByIdFilm(id);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        // Проверка даты релиза
        if (film.getReleaseDate() != null &&
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ConditionsNotMetException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        // Проверка MPA
        if (film.getMpa() == null || film.getMpa().getId() == null) {
            throw new ConditionsNotMetException("Рейтинг MPA должен быть указан");
        }

        // Проверяем, что MPA существует в БД
        Mpa mpa = mpaDbStorage.findById(film.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("Рейтинг MPA с id = " + film.getMpa().getId() + " не найден"));
        film.setMpa(mpa);

        // Проверка жанров - используем LinkedHashSet для сохранения порядка
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Genre> validGenres = new LinkedHashSet<>();  // ← LinkedHashSet
            for (Genre genre : film.getGenres()) {
                if (genre.getId() == null) {
                    throw new ConditionsNotMetException("ID жанра должен быть указан");
                }
                Genre foundGenre = genreDbStorage.findByIdGenre(genre.getId())
                        .orElseThrow(() -> new NotFoundException("Жанр с id = " + genre.getId() + " не найден"));
                validGenres.add(foundGenre);
            }
            film.setGenres(validGenres);
        } else {
            film.setGenres(new LinkedHashSet<>());  // ← LinkedHashSet
        }

        validateFilm(film);
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        // Проверяем, что фильм существует
        filmService.findByIdFilm(newFilm.getId());

        // Если указан MPA, проверяем его существование
        if (newFilm.getMpa() != null && newFilm.getMpa().getId() != null) {
            Mpa mpa = mpaDbStorage.findById(newFilm.getMpa().getId())
                    .orElseThrow(() -> new NotFoundException("Рейтинг MPA с id = " + newFilm.getMpa().getId() + " не найден"));
            newFilm.setMpa(mpa);
        }

        // Если указаны жанры, проверяем их существование - используем LinkedHashSet
        if (newFilm.getGenres() != null && !newFilm.getGenres().isEmpty()) {
            Set<Genre> validGenres = new LinkedHashSet<>();  // ← LinkedHashSet
            for (Genre genre : newFilm.getGenres()) {
                if (genre.getId() == null) {
                    throw new ConditionsNotMetException("ID жанра должен быть указан");
                }
                Genre foundGenre = genreDbStorage.findByIdGenre(genre.getId())
                        .orElseThrow(() -> new NotFoundException("Жанр с id = " + genre.getId() + " не найден"));
                validGenres.add(foundGenre);
            }
            newFilm.setGenres(validGenres);
        }

        validateUpdateFilm(newFilm);
        return filmService.updateFilm(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopular(count);
    }

    // ========== ВАЛИДАЦИЯ ДЛЯ FILM ==========
    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ConditionsNotMetException("Название не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ConditionsNotMetException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ConditionsNotMetException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ConditionsNotMetException("Продолжительность фильма должна быть положительным числом");
        }
    }

    private void validateUpdateFilm(Film film) {
        if (film.getName() != null && film.getName().isBlank()) {
            throw new ConditionsNotMetException("Название не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ConditionsNotMetException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ConditionsNotMetException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            throw new ConditionsNotMetException("Продолжительность фильма должна быть положительным числом");
        }
    }
}