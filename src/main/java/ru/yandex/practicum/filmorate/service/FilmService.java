package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final UserService userService;
    private final FilmStorage filmStorage;

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public User findByIdUser(long id) {
        return userService.findByIdUser(id);
    }

    public Film findByIdFilm(long id) {
        return filmStorage.findById(id).orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));
    }

    public Collection<Film> getPopular(int count) {
        return filmStorage.getFilms().stream().sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size()).limit(count).collect(Collectors.toList());
    }

    public void addLike(long filmId, long userId) {
        Film film = findByIdFilm(filmId);
        findByIdUser(userId);
        film.getLikes().add(userId);
    }

    public void removeLike(long filmId, long userId) {
        Film film = findByIdFilm(filmId);
        findByIdUser(userId);
        film.getLikes().remove(userId);
    }
}
