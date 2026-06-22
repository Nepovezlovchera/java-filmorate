package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.FriendsRowMapper;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public class FriendsDbStorage extends BaseStorage<Friends> {

    private final UserRowMapper userRowMapper;
    private static final int STATUS_UNCONFIRMED = 1;
    private static final int STATUS_CONFIRMED = 2;
    private static final String FIND_ALL = "SELECT * FROM friends";
    private static final String FIND_BY_ID = "SELECT * FROM friends WHERE friend_id = ?";
    private static final String ADD_FRIEND = "INSERT INTO friends(user_id, other_user_id, status_id) VALUES (?, ?, ?)";
    private static final String REMOVE_FRIEND = "DELETE FROM friends WHERE user_id = ? AND other_user_id = ?";
    private static final String CONFIRM_FRIEND = "UPDATE friends SET status_id = ? WHERE user_id = ? AND other_user_id = ?";
    private static final String CHECK_FRIEND = "SELECT COUNT(*) FROM friends WHERE user_id = ? AND other_user_id = ?";
    private static final String GET_FRIENDS = "SELECT u.* FROM users AS u " +
            "JOIN friends AS f ON u.user_id = f.other_user_id " +
            "WHERE f.user_id = ?";
    private static final String UPDATE_FRIEND_STATUS = "UPDATE friends SET status_id = ? WHERE user_id = ? " +
            "AND other_user_id = ?";
    private static final String GET_COMMON_FRIENDS = "SELECT DISTINCT u.* FROM friends f1 " +
            "JOIN friends f2 ON f1.other_user_id = f2.other_user_id JOIN users u ON u.user_id = f1.other_user_id " +
            "WHERE f1.user_id = ? AND f2.user_id = ? " +
            "ORDER BY u.user_id";

    public FriendsDbStorage(JdbcTemplate jdbc, FriendsRowMapper mapper, UserRowMapper userRowMapper) {
        super(jdbc, mapper);
        this.userRowMapper = userRowMapper;
    }

    public List<Friends> findAll() {
        return findMany(FIND_ALL);
    }

    public Optional<Friends> findById(Long id) {
        return findOne(FIND_BY_ID, id);
    }

    public void addFriend(long userId, long friendId) {
        if (userId == friendId) {
            return;
        }

        boolean reverseExists = checkFriendship(friendId, userId);

        if (reverseExists) {
            jdbc.update(ADD_FRIEND, userId, friendId, STATUS_CONFIRMED);
            updateFriendStatus(friendId, userId, STATUS_CONFIRMED);
        } else {
            jdbc.update(ADD_FRIEND, userId, friendId, STATUS_UNCONFIRMED);
        }
    }

    public void removeFriend(long userId, long friendId) {
        jdbc.update(REMOVE_FRIEND, userId, friendId);
    }

    public void confirmFriend(long userId, long friendId) {
        update(CONFIRM_FRIEND, STATUS_CONFIRMED, userId, friendId);
    }

    public boolean checkFriendship(long userId, long friendId) {
        Integer count = checkFriendship(CHECK_FRIEND, Integer.class, userId, friendId);
        return count != null && count > 0;
    }

    public List<User> getFriends(long userId) {
        return findManyWithMapper(GET_FRIENDS, userRowMapper, userId);
    }

    public void updateFriendStatus(long userId, long friendId, int statusId) {
        update(UPDATE_FRIEND_STATUS, statusId, userId, friendId);
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        return findManyWithMapper(GET_COMMON_FRIENDS, userRowMapper, userId, otherId);
    }
}