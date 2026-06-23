package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.mappers.LikeRowMapper;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmGenresStorage;
import ru.yandex.practicum.filmorate.storage.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({LikeDbStorage.class, LikeRowMapper.class, UserDbStorage.class, UserRowMapper.class,
        FilmDbStorage.class, FilmRowMapper.class, FilmGenresStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LikeDbStorageTest {
    private final LikeDbStorage likeDbStorage;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private Long userId;
    private Long filmId;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setEmail("user@mail.ru");
        user.setLogin("user");
        user.setName("User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userId = userDbStorage.createUser(user).getId();

        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1L);
        film.setMpa(mpa);
        filmId = filmDbStorage.createFilm(film).getId();
    }

    @Test
    void testAddLike() {
        Long likeCount = likeDbStorage.create(userId, filmId);

        assertThat(likeCount).isEqualTo(1);
    }

    @Test
    void testAddLikeDuplicate() {
        likeDbStorage.create(userId, filmId);
        Long likeCount = likeDbStorage.create(userId, filmId);

        assertThat(likeCount).isEqualTo(1);
    }

    @Test
    void testCountLikes() {
        likeDbStorage.create(userId, filmId);

        Long count = likeDbStorage.countLikes(filmId);

        assertThat(count).isEqualTo(1);
    }
}