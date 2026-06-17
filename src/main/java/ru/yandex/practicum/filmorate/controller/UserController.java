package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable long id) {
        return userService.findByIdUser(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        validate(user);
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        validateUpdate(newUser);
        return userService.updateUser(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable long id) {
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    private void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Электронная почта не может быть пустой");
        }
        if (!user.getEmail().contains("@")) {
            throw new ConditionsNotMetException("Электронная почта должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ConditionsNotMetException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            throw new ConditionsNotMetException("Логин не может содержать пробелы");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
        }
    }

    private void validateUpdate(User user) {
        if (user.getEmail() != null && !user.getEmail().contains("@")) {
            throw new ConditionsNotMetException("Электронная почта должна содержать символ @");
        }
        if (user.getLogin() != null && user.getLogin().isBlank()) {
            throw new ConditionsNotMetException("Логин не может быть пустым");
        }
        if (user.getLogin() != null && user.getLogin().contains(" ")) {
            throw new ConditionsNotMetException("Логин не может содержать пробелы");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
        }
    }
}
