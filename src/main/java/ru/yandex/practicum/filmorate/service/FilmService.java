package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmDbStorage filmdbStorage;
    private final LikeDbStorage likeDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final FilmGenresStorage filmGenresStorage;
    private final UserService userService;

    public FilmService(
            @Qualifier("filmDbStorage") FilmDbStorage filmdbStorage,
            LikeDbStorage likeDbStorage,
            MpaDbStorage mpaDbStorage,
            GenreDbStorage genreDbStorage,
            FilmGenresStorage filmGenresStorage,
            UserService userService) {

        this.filmdbStorage = filmdbStorage;
        this.likeDbStorage = likeDbStorage;
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
        this.filmGenresStorage = filmGenresStorage;
        this.userService = userService;
    }

    public Collection<FilmDto> getFilms() {
        return filmdbStorage.getFilms().stream()
                .map(this::filmWithGenres)
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto findByIdFilm(long id) {
        Film film = filmdbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));

        Film enrichedFilm = filmWithGenres(film);
        return FilmMapper.mapToFilmDto(enrichedFilm);
    }

    public FilmDto createFilm(Film film) {
        validateMpa(film);
        validateGenres(film);

        Film created = filmdbStorage.createFilm(film);
        return FilmMapper.mapToFilmDto(filmWithGenres(created));
    }

    public FilmDto updateFilm(Film film) {
        findByIdFilm(film.getId());

        if (film.getMpa() != null) {
            validateMpa(film);
        }
        if (film.getGenres() != null) {
            validateGenres(film);
        }

        Film updated = filmdbStorage.updateFilm(film);
        return FilmMapper.mapToFilmDto(filmWithGenres(updated));
    }


    private Film filmWithGenres(Film film) {
        if (film == null) return null;

        List<Genre> genres = filmGenresStorage.getGenresByFilmId(film.getId());
        film.setGenres(new LinkedHashSet<>(genres));
        return film;
    }

    private void validateMpa(Film film) {
        if (film.getMpa() == null || film.getMpa().getId() == null) {
            throw new ConditionsNotMetException("Рейтинг MPA должен быть указан");
        }
        Mpa mpa = mpaDbStorage.findById(film.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("Рейтинг MPA с id = " + film.getMpa().getId() + " не найден"));
        film.setMpa(mpa);
    }

    private void validateGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            film.setGenres(new LinkedHashSet<>());
            return;
        }

        Set<Genre> validGenres = new LinkedHashSet<>();
        for (Genre genre : film.getGenres()) {
            if (genre.getId() == null) {
                throw new ConditionsNotMetException("ID жанра должен быть указан");
            }
            Genre found = genreDbStorage.findByIdGenre(genre.getId())
                    .orElseThrow(() -> new NotFoundException("Жанр с id = " + genre.getId() + " не найден"));
            validGenres.add(found);
        }
        film.setGenres(validGenres);
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

    public Collection<FilmDto> getPopular(int count) {
        return filmdbStorage.getPopular(count).stream()
                .map(this::filmWithGenres)
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }
}