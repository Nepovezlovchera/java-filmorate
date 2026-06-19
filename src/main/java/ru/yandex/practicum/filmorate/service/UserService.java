package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.FriendsDbStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Qualifier
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FriendsDbStorage friendsDbStorage;

    public UserService(
            @Qualifier("userDbStorage") UserStorage userStorage,   // ← важно
            FriendsDbStorage friendsDbStorage) {

        this.userStorage = userStorage;
        this.friendsDbStorage = friendsDbStorage;
    }

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
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
    }

    public void addFriend(long userId, long friendId) {
        findByIdUser(userId);
        findByIdUser(friendId);
        friendsDbStorage.addFriend(userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        findByIdUser(userId);
        findByIdUser(friendId);
        friendsDbStorage.removeFriend(userId, friendId);
    }

    public Collection<User> getAllFriends(long userId) {
        findByIdUser(userId);
        return friendsDbStorage.getFriends(userId);
    }

    public Collection<User> getCommonFriends(long userId, long otherId) {
        findByIdUser(userId);
        findByIdUser(otherId);

        Set<Long> userFriends = friendsDbStorage.getFriends(userId).stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        Set<Long> otherFriends = friendsDbStorage.getFriends(otherId).stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        Set<Long> commonFriendIds = new HashSet<>(userFriends);
        commonFriendIds.retainAll(otherFriends);

        return commonFriendIds.stream()
                .map(id -> userStorage.findById(id)
                        .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден")))
                .collect(Collectors.toList());
    }
}