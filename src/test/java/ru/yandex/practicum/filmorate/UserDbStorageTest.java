package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, UserRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    void testCreateUser() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testLogin");
        user.setName("Test Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User created = userStorage.createUser(user);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getEmail()).isEqualTo("test@mail.ru");
        assertThat(created.getLogin()).isEqualTo("testLogin");
        assertThat(created.getName()).isEqualTo("Test Name");
        assertThat(created.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    void testFindUserById() {
        User user = new User();
        user.setEmail("find@mail.ru");
        user.setLogin("findLogin");
        user.setName("Find Name");
        user.setBirthday(LocalDate.of(1995, 5, 5));
        User created = userStorage.createUser(user);

        Optional<User> found = userStorage.findById(created.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("find@mail.ru");
        assertThat(found.get().getLogin()).isEqualTo("findLogin");
    }

    @Test
    void testFindUserByIdNotFound() {
        Optional<User> found = userStorage.findById(999L);
        assertThat(found).isEmpty();
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setEmail("old@mail.ru");
        user.setLogin("oldLogin");
        user.setName("Old Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User created = userStorage.createUser(user);

        created.setEmail("new@mail.ru");
        created.setLogin("newLogin");
        created.setName("New Name");
        created.setBirthday(LocalDate.of(1995, 5, 5));

        User updated = userStorage.updateUser(created);

        assertThat(updated.getEmail()).isEqualTo("new@mail.ru");
        assertThat(updated.getLogin()).isEqualTo("newLogin");
        assertThat(updated.getName()).isEqualTo("New Name");
        assertThat(updated.getBirthday()).isEqualTo(LocalDate.of(1995, 5, 5));
    }

    @Test
    void testGetUsers() {
        User user1 = new User();
        user1.setEmail("user1@mail.ru");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.createUser(user1);

        User user2 = new User();
        user2.setEmail("user2@mail.ru");
        user2.setLogin("user2");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(1995, 5, 5));
        userStorage.createUser(user2);

        Collection<User> users = userStorage.getUsers();

        assertThat(users).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void testCreateUserWithMinimalFields() {
        User user = new User();
        user.setEmail("minimal@mail.ru");
        user.setLogin("minimalLogin");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User created = userStorage.createUser(user);
        assertThat(created.getId()).isNotNull();
        assertThat(created.getEmail()).isEqualTo("minimal@mail.ru");
        assertThat(created.getLogin()).isEqualTo("minimalLogin");
        assertThat(created.getName()).isNull();
    }

    @Test
    void testUpdateUserWithNullFields() {
        User user = new User();
        user.setEmail("update@mail.ru");
        user.setLogin("updateLogin");
        user.setName("Update Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User created = userStorage.createUser(user);

        created.setName(null);
        User updated = userStorage.updateUser(created);

        assertThat(updated.getName()).isNull();
    }

    @Test
    void testFindAllUsersEmpty() {
        Collection<User> users = userStorage.getUsers();
        assertThat(users).isNotNull();
    }
}