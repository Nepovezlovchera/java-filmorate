package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> getUsers();

    User createUser(User user);

    User updateUser(User newUser);

    Optional<User> findById(long id);
}
