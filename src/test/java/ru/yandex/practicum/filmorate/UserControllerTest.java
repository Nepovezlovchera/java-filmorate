package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private UserController controller;

    @BeforeEach
    void contextLoads() {
        controller = new UserController();
    }

    private User makeUserForTest(String email, String login, String name, LocalDate birthday) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(birthday);
        return user;
    }

    @Test
    void shouldCreateUserSuccessfully() {
        User user = makeUserForTest("test@mail.ru", "login", "Имя", LocalDate.of(1990, 1, 1));
        User created = controller.createUser(user);
        assertNotNull(created.getId());
        assertEquals("test@mail.ru", created.getEmail());
    }

    @Test
    void shouldThrowWhenEmailIsNull() {
        User user = makeUserForTest(null, "login", "Имя", LocalDate.of(1990, 1, 1));
        assertThrows(ConditionsNotMetException.class, () -> controller.createUser(user));
    }

    @Test
    void shouldThrowWhenEmailIsBlank() {
        User user = makeUserForTest("   ", "login", "Имя", LocalDate.of(1990, 1, 1));
        assertThrows(ConditionsNotMetException.class, () -> controller.createUser(user));
    }

    @Test
    void shouldThrowWhenEmailHasNoAtSign() {
        User user = makeUserForTest("testmail.ru", "login", "Имя", LocalDate.of(1990, 1, 1));
        assertThrows(ConditionsNotMetException.class, () -> controller.createUser(user));
    }

    @Test
    void shouldThrowWhenLoginIsNull() {
        User user = makeUserForTest("test@mail.ru", null, "Имя", LocalDate.of(1990, 1, 1));
        assertThrows(ConditionsNotMetException.class, () -> controller.createUser(user));
    }

    @Test
    void shouldThrowWhenLoginIsBlank() {
        User user = makeUserForTest("test@mail.ru", "   ", "Имя", LocalDate.of(1990, 1, 1));
        assertThrows(ConditionsNotMetException.class, () -> controller.createUser(user));
    }

    @Test
    void shouldThrowWhenLoginContainsSpace() {
        User user = makeUserForTest("test@mail.ru", "log in", "Имя", LocalDate.of(1990, 1, 1));
        assertThrows(ConditionsNotMetException.class, () -> controller.createUser(user));
    }

    @Test
    void shouldUseLoginAsNameWhenNameIsNull() {
        User user = makeUserForTest("test@mail.ru", "login", null, LocalDate.of(1990, 1, 1));
        User created = controller.createUser(user);
        assertEquals("login", created.getName());
    }

    @Test
    void shouldUseLoginAsNameWhenNameIsBlank() {
        User user = makeUserForTest("test@mail.ru", "login", "   ", LocalDate.of(1990, 1, 1));
        User created = controller.createUser(user);
        assertEquals("login", created.getName());
    }

    @Test
    void shouldThrowWhenBirthdayIsInFuture() {
        User user = makeUserForTest("test@mail.ru", "login", "Имя", LocalDate.now().plusDays(1));
        assertThrows(ConditionsNotMetException.class, () -> controller.createUser(user));
    }

    @Test
    void shouldPassWhenBirthdayIsToday() {
        User user = makeUserForTest("test@mail.ru", "login", "Имя", LocalDate.now());
        assertDoesNotThrow(() -> controller.createUser(user));
    }

    @Test
    void shouldThrowWhenUpdateWithoutId() {
        User user = makeUserForTest("test@mail.ru", "login", "Имя", LocalDate.of(1990, 1, 1));
        assertThrows(ConditionsNotMetException.class, () -> controller.updateUser(user));
    }

    @Test
    void shouldThrowWhenUpdateNotExistingUser() {
        User user = makeUserForTest("test@mail.ru", "login", "Имя", LocalDate.of(1990, 1, 1));
        user.setId(999L);
        assertThrows(NotFoundException.class, () -> controller.updateUser(user));
    }


    @Test
    void shouldThrowWhenUpdateEmailHasNoAtSign() {
        User created = controller.createUser(makeUserForTest("test@mail.ru", "login", "Имя", LocalDate.of(1990, 1, 1)));
        User update = makeUserForTest("testmail.ru", "login", "Имя", LocalDate.of(1990, 1, 1));
        update.setId(created.getId());
        assertThrows(ConditionsNotMetException.class, () -> controller.updateUser(update));
    }

    @Test
    void shouldThrowWhenUpdateLoginContainsSpace() {
        User created = controller.createUser(makeUserForTest("test@mail.ru", "login", "Имя", LocalDate.of(1990, 1, 1)));
        User update = makeUserForTest("test@mail.ru", "log in", "Имя", LocalDate.of(1990, 1, 1));
        update.setId(created.getId());
        assertThrows(ConditionsNotMetException.class, () -> controller.updateUser(update));
    }

    @Test
    void shouldThrowWhenUpdateBirthdayIsInFuture() {
        User created = controller.createUser(makeUserForTest("test@mail.ru", "login", "Имя", LocalDate.of(1990, 1, 1)));
        User update = makeUserForTest("test@mail.ru", "login", "Имя", LocalDate.now().plusDays(1));
        update.setId(created.getId());
        assertThrows(ConditionsNotMetException.class, () -> controller.updateUser(update));
    }

    @Test
    void shouldUseLoginAsNameWhenUpdateNameIsNull() {
        User created = controller.createUser(makeUserForTest("test@mail.ru", "login", "Имя", LocalDate.of(1990, 1, 1)));
        User update = makeUserForTest("test@mail.ru", "newlogin", null, LocalDate.of(1990, 1, 1));
        update.setId(created.getId());
        User result = controller.updateUser(update);
        assertEquals("newlogin", result.getName());
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        User created = controller.createUser(makeUserForTest("old@mail.ru", "oldlogin", "Старое", LocalDate.of(1990, 1, 1)));
        User update = makeUserForTest("new@mail.ru", "newlogin", "Новое", LocalDate.of(1995, 5, 5));
        update.setId(created.getId());
        User result = controller.updateUser(update);
        assertEquals("new@mail.ru", result.getEmail());
        assertEquals("newlogin", result.getLogin());
        assertEquals("Новое", result.getName());
        assertEquals(LocalDate.of(1995, 5, 5), result.getBirthday());
    }
}