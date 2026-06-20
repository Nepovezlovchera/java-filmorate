package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.MpaController;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class MpaControllerTest {

    @Autowired
    private MpaController mpaController;

    @Test
    void shouldGetAllMpa() {
        List<Mpa> mpaList = mpaController.getMpa();

        assertThat(mpaList)
                .isNotNull()
                .hasSize(5)
                .extracting(Mpa::getId)
                .containsExactly(1L, 2L, 3L, 4L, 5L);

        assertThat(mpaList.get(0).getName()).isEqualTo("G");
        assertThat(mpaList.get(1).getName()).isEqualTo("PG");
        assertThat(mpaList.get(2).getName()).isEqualTo("PG-13");
        assertThat(mpaList.get(3).getName()).isEqualTo("R");
        assertThat(mpaList.get(4).getName()).isEqualTo("NC-17");
    }

    @Test
    void shouldGetMpaById() {
        Mpa mpa = mpaController.getMpaById(1L);

        assertThat(mpa)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "G");
    }

    @Test
    void shouldReturn404WhenMpaNotFound() {
        assertThrows(RuntimeException.class, () -> {
            mpaController.getMpaById(999L);
        });
    }
}