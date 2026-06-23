package ru.yandex.practicum.filmorate.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendsRowMapper implements RowMapper<Friends> {
    @Override
    public Friends mapRow(ResultSet rs, int rowNum) throws SQLException {
        Friends fr = new Friends();
        fr.setFriendId(rs.getLong("friend_id"));
        fr.setUserId(rs.getLong("user_id"));
        fr.setOtherUserId(rs.getLong("other_user_id"));
        int statusId = rs.getInt("status_id");
        if (statusId == 2) {
            fr.setStatus(FriendshipStatus.CONFIRMED);
        } else {
            fr.setStatus(FriendshipStatus.UNCONFIRMED);
        }

        return fr;
    }
}
