package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {

    public static Film mapToFilm(NewFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        film.setMpa(request.getMpa());

        if (request.getGenre() != null) {
            film.setGenres(new HashSet<>((Collection) request.getGenre()));
        } else if (request.getGenre() != null) {
            film.setGenres(Set.of(request.getGenre()));
        }

        return film;
    }

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        dto.setMpa(film.getMpa());

        if (film.getGenres() != null) {
            dto.setGenre((Genre) film.getGenres());
        }

        return dto;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {
        if (request.hasDescription()) film.setDescription(request.getDescription());
        if (request.hasName()) film.setName(request.getName());
        if (request.hasReleaseDate()) film.setReleaseDate(request.getReleaseDate());
        if (request.hasDuration()) film.setDuration(request.getDuration());
        if (request.hasMpa() || request.hasMpa()) film.setMpa(request.getMpa());

        if (request.hasGenres()) {
            film.setGenres(request.getGenres());
        }

        return film;
    }
}