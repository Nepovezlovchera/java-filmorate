package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.mappers.FriendsRowMapper;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FriendsDbStorage.class, FriendsRowMapper.class, UserRowMapper.class, UserDbStorage.class})
@Sql(scripts = {"/schema.sql", "/data.sql"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FriendsDbStorageTest {
    private final FriendsDbStorage friendsDbStorage;
    private final UserDbStorage userDbStorage;
    private Long userId1;
    private Long userId2;

    @BeforeEach
    void setUp() {
        User user1 = new User();
        user1.setEmail("user1@mail.ru");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        userId1 = userDbStorage.createUser(user1).getId();

        User user2 = new User();
        user2.setEmail("user2@mail.ru");
        user2.setLogin("user2");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(1995, 5, 5));
        userId2 = userDbStorage.createUser(user2).getId();
    }

    @Test
    void testAddFriend() {
        friendsDbStorage.addFriend(userId1, userId2);

        Collection<User> friends = friendsDbStorage.getFriends(userId1);
        assertThat(friends).hasSize(1);
        assertThat(friends.iterator().next().getId()).isEqualTo(userId2);
    }

    @Test
    void testAddFriendOnlyOneWay() {
        friendsDbStorage.addFriend(userId1, userId2);

        Collection<User> friendsOfUser1 = friendsDbStorage.getFriends(userId1);
        Collection<User> friendsOfUser2 = friendsDbStorage.getFriends(userId2);

        assertThat(friendsOfUser1).hasSize(1);
        assertThat(friendsOfUser2).isEmpty();
    }

    @Test
    void testRemoveFriend() {
        friendsDbStorage.addFriend(userId1, userId2);
        friendsDbStorage.removeFriend(userId1, userId2);

        Collection<User> friends = friendsDbStorage.getFriends(userId1);
        assertThat(friends).isEmpty();
    }
}