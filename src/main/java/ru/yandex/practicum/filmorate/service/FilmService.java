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
        Collection<Film> films = filmdbStorage.getFilms();
        enrichFilmsWithGenres(films);
        return films.stream()
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

        Set<Long> genreIds = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        if (genreIds.contains(null)) {
            throw new ConditionsNotMetException("ID жанра должен быть указан");
        }

        List<Genre> foundGenres = genreDbStorage.findAllByIds(genreIds);

        if (foundGenres.size() != genreIds.size()) {
            Set<Long> foundIds = foundGenres.stream().map(Genre::getId).collect(Collectors.toSet());
            Long missingId = genreIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .findFirst()
                    .orElse(null);

            throw new NotFoundException("Жанр с id = " + missingId + " не найден");
        }

        film.setGenres(new LinkedHashSet<>(foundGenres));
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
        Collection<Film> films = filmdbStorage.getFilms();
        enrichFilmsWithGenres(films);

        return films.stream()
                .sorted((f1, f2) -> Long.compare(
                        likeDbStorage.countLikes(f2.getId()),
                        likeDbStorage.countLikes(f1.getId())))
                .limit(count)
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    private void enrichFilmsWithGenres(Collection<Film> films) {
        if (films == null || films.isEmpty()) return;

        for (Film film : films) {
            List<Genre> genres = filmGenresStorage.getGenresByFilmId(film.getId());
            film.setGenres(new LinkedHashSet<>(genres));
        }
    }
}