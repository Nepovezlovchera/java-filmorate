package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.dto.NewMpaRequest;
import ru.yandex.practicum.filmorate.model.Mpa;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MpaMapper {
    public static Mpa mapToMPA(NewMpaRequest request) {
        Mpa mpa = new Mpa();
        mpa.setId(request.getMpaId());
        mpa.setName(request.getMpaName());

        return mpa;
    }

    public static MpaDto mapToMPADto(Mpa mpa) {
        MpaDto dto = new MpaDto();
        dto.setMpaId(mpa.getId());
        dto.setMpaName(mpa.getName());

        return dto;
    }
}
