package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GenreDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long genreId;
    private String genreName;
}
