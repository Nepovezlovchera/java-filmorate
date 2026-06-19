package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaDbStorage mpaDbStorage;

    @GetMapping
    public List<Mpa> getMpa() {
        return mpaDbStorage.findAll();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable Long id) {
        return mpaDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг MPA с id = " + id + " не найден"));
    }
}