package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeDbStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Qualifier
@Service
public class FilmService {

    private final UserService userService;
    private final FilmStorage filmStorage;
    private final LikeDbStorage likeDbStorage;

    @Autowired
    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,   // ← важно
            LikeDbStorage likeDbStorage,
            UserService userService) {

        this.filmStorage = filmStorage;
        this.likeDbStorage = likeDbStorage;
        this.userService = userService;
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film findByIdFilm(long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));
    }

    public void addLike(long filmId, long userId) {
        findByIdFilm(filmId);
        userService.findByIdUser(userId);
        likeDbStorage.create(userId, filmId);
    }

    public void removeLike(long filmId, long userId) {
        findByIdFilm(filmId);
        userService.findByIdUser(userId);
    }

    public Collection<Film> getPopular(int count) {
        return filmStorage.getFilms().stream()
                .sorted((f1, f2) -> Long.compare(
                        likeDbStorage.countLikes(f2.getId()),
                        likeDbStorage.countLikes(f1.getId())))
                .limit(count)
                .collect(Collectors.toList());
    }
}