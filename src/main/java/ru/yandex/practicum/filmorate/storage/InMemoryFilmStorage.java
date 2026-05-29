package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private static final int ZERO = 0;

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (!films.containsKey(newFilm.getId())) {
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }
        Film oldFilm = films.get(newFilm.getId());
        if (newFilm.getName() != null) oldFilm.setName(newFilm.getName());
        if (newFilm.getDescription() != null) oldFilm.setDescription(newFilm.getDescription());
        if (newFilm.getReleaseDate() != null) oldFilm.setReleaseDate(newFilm.getReleaseDate());
        if (newFilm.getDuration() > ZERO) oldFilm.setDuration(newFilm.getDuration());
        return oldFilm;
    }

    @Override
    public Optional<Film> findById(long id) {
        return Optional.ofNullable(films.get(id));
    }



    private long getNextId() {
        return films.keySet().stream().mapToLong(id -> id).max().orElse(0) + 1;
    }
}
