    package ru.yandex.practicum.filmorate.dto;

    import com.fasterxml.jackson.annotation.JsonProperty;
    import lombok.Data;
    import ru.yandex.practicum.filmorate.model.Genre;
    import ru.yandex.practicum.filmorate.model.Mpa;

    import java.time.LocalDate;

    @Data
    public class FilmDto {
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private long id;
        private String name;
        private String description;
        private Double duration;
        private Genre genre;
        private Mpa mpa;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private LocalDate releaseDate = LocalDate.now();
    }
