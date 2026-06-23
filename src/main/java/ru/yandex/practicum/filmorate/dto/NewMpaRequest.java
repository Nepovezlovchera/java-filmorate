package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

@Data
public class NewMpaRequest {
    private long mpaId;
    private String mpaName;
}