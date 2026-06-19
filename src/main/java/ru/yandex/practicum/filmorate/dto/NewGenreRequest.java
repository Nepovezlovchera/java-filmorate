package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

@Data
public class NewGenreRequest {
    private Long genreId;
    private String genreName;
}
