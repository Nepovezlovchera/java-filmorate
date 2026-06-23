package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class GenreService {

    private final GenreDbStorage genreDbStorage;

    public GenreService(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    public Collection<GenreDto> getGenres() {
        return genreDbStorage.findAllGenre().stream()
                .map(GenreMapper::mapToGenreDto)
                .collect(Collectors.toList());
    }

    public GenreDto getGenreById(Long id) {
        Genre genre = genreDbStorage.findByIdGenre(id)
                .orElseThrow(() -> new NotFoundException("Жанр с id = " + id + " не найден"));
        return GenreMapper.mapToGenreDto(genre);
    }
}