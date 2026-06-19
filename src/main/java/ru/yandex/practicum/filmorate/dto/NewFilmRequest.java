package ru.yandex.practicum.filmorate.dto;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;

@Setter
@Getter
public class NewFilmRequest {
    private Long id;
    private Genre genre;
    private Mpa mpa;
    private String name;
    private String description;
    private int duration;
    private LocalDate releaseDate;
}
