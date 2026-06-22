package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;
    private final FriendsDbStorage friendsDbStorage;

    public UserService(
            @Qualifier("userDbStorage") UserStorage userStorage,
            FriendsDbStorage friendsDbStorage) {

        this.userStorage = userStorage;
        this.friendsDbStorage = friendsDbStorage;
    }

    public Collection<UserDto> getUsers() {
        return userStorage.getUsers().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto createUser(User user) {
        User created = userStorage.createUser(user);
        return UserMapper.mapToUserDto(created);
    }

    public UserDto updateUser(User user) {
        User updated = userStorage.updateUser(user);
        return UserMapper.mapToUserDto(updated);
    }

    public UserDto findByIdUser(long id) {
        User user = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
        return UserMapper.mapToUserDto(user);
    }

    public void addFriend(long userId, long friendId) {
        findByIdUser(userId);   // проверка существования
        findByIdUser(friendId);
        friendsDbStorage.addFriend(userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        findByIdUser(userId);
        findByIdUser(friendId);
        friendsDbStorage.removeFriend(userId, friendId);
    }

    public Collection<UserDto> getAllFriends(long userId) {
        findByIdUser(userId);
        return friendsDbStorage.getFriends(userId).stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public Collection<UserDto> getCommonFriends(long userId, long otherId) {
        findByIdUser(userId);
        findByIdUser(otherId);

        return friendsDbStorage.getCommonFriends(userId, otherId).stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }
}