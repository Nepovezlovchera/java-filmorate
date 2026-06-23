package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friends {
    private Long friendId;
    private Long userId;
    private Long otherUserId;
    private FriendshipStatus status;
}
