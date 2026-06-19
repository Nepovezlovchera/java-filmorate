package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({MpaDbStorage.class, MpaRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDbStorageTest {
    private final MpaDbStorage mpaDbStorage;

    @Test
    void testFindAllMpa() {
        List<Mpa> mpaList = mpaDbStorage.findAll();

        assertThat(mpaList).hasSize(5);
        assertThat(mpaList.get(0).getName()).isEqualTo("G");
        assertThat(mpaList.get(1).getName()).isEqualTo("PG");
        assertThat(mpaList.get(2).getName()).isEqualTo("PG-13");
        assertThat(mpaList.get(3).getName()).isEqualTo("R");
        assertThat(mpaList.get(4).getName()).isEqualTo("NC-17");
    }

    @Test
    void testFindMpaById() {
        Optional<Mpa> mpa = mpaDbStorage.findById(1L);

        assertThat(mpa).isPresent();
        assertThat(mpa.get().getName()).isEqualTo("G");
    }

    @Test
    void testFindMpaByIdNotFound() {
        Optional<Mpa> mpa = mpaDbStorage.findById(999L);
        assertThat(mpa).isEmpty();
    }
}