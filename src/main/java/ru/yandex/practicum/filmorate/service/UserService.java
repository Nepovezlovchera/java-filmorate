package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User findByIdUser(long id) {
        return userStorage.findById(id).
                orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
    }

    public Collection<User> getAllFriends(long userId) {
        User user = findByIdUser(userId);
        return user.getFriends().stream()
                .map(this::findByIdUser)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(long userId, long otherId) {
        User user = findByIdUser(userId);
        User other = findByIdUser(otherId);
        return user.getFriends().stream().
                filter(other.getFriends()::contains)
                .map(this::findByIdUser)
                .collect(Collectors.toList());
    }

    public void addFriend(long userId, long friendId) {
        User user = findByIdUser(userId);
        User friend = findByIdUser(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(long userId, long friendId) {
        User user = findByIdUser(userId);
        User friend = findByIdUser(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }
}
