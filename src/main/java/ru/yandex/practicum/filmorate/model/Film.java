    package ru.yandex.practicum.filmorate.model;

    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.NotNull;
    import jakarta.validation.constraints.Positive;
    import jakarta.validation.constraints.Size;
    import lombok.Data;

    import java.time.LocalDate;
    import java.util.HashSet;
    import java.util.LinkedHashSet;
    import java.util.Set;

    @Data
    public class Film {
        private Long id;

        @NotBlank(message = "Название не может быть пустым")
        private String name;

        @Size(max = 200, message = "Максимальная длина описания — 200 символов")
        private String description;

        @NotNull(message = "Дата релиза должна быть указана")
        private LocalDate releaseDate;

        @Positive(message = "Продолжительность фильма должна быть положительным числом")
        private double duration;
        private Set<Long> likes = new HashSet<>();

        @NotNull(message = "Жанр должен быть указан")
        private Set<Genre> genres = new LinkedHashSet<>();

        @NotNull(message = "Рейтинг Mpa должен быть указан")
        private Mpa mpa;
    }