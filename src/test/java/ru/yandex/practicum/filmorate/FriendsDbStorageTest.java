package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
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
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FriendsDbStorageTest {
    private final FriendsDbStorage friendsDbStorage;
    private final UserDbStorage userDbStorage;
    private Long userId1;
    private Long userId2;
    private Long userId3;

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

        User user3 = new User();
        user3.setEmail("user3@mail.ru");
        user3.setLogin("user3");
        user3.setName("User Three");
        user3.setBirthday(LocalDate.of(2000, 1, 1));
        userId3 = userDbStorage.createUser(user3).getId();
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

    @Test
    void testRemoveFriendOnlyOneWay() {
        friendsDbStorage.addFriend(userId1, userId2);

        assertThat(friendsDbStorage.getFriends(userId1)).hasSize(1);
        assertThat(friendsDbStorage.getFriends(userId2)).isEmpty();

        friendsDbStorage.removeFriend(userId2, userId1);

        Collection<User> friendsOfUser1 = friendsDbStorage.getFriends(userId1);
        assertThat(friendsOfUser1).hasSize(1);
        assertThat(friendsOfUser1.iterator().next().getId()).isEqualTo(userId2);

        friendsDbStorage.removeFriend(userId1, userId2);

        assertThat(friendsDbStorage.getFriends(userId1)).isEmpty();
        assertThat(friendsDbStorage.getFriends(userId2)).isEmpty();
    }

    @Test
    void testAddFriendSelf() {
        friendsDbStorage.addFriend(userId1, userId1);

        Collection<User> friends = friendsDbStorage.getFriends(userId1);
        assertThat(friends).isEmpty();
    }

    @Test
    void testGetFriendsEmpty() {
        Collection<User> friends = friendsDbStorage.getFriends(userId1);
        assertThat(friends).isEmpty();
    }

    @Test
    void testAddMultipleFriends() {
        friendsDbStorage.addFriend(userId1, userId2);
        friendsDbStorage.addFriend(userId1, userId3);

        Collection<User> friends = friendsDbStorage.getFriends(userId1);
        assertThat(friends).hasSize(2);
        assertThat(friends)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(userId2, userId3);
    }

    @Test
    void testMutualFriendship() {
        friendsDbStorage.addFriend(userId1, userId2);

        assertThat(friendsDbStorage.getFriends(userId1)).hasSize(1);
        assertThat(friendsDbStorage.getFriends(userId2)).isEmpty();

        friendsDbStorage.addFriend(userId2, userId1);

        Collection<User> friendsOfUser1 = friendsDbStorage.getFriends(userId1);
        Collection<User> friendsOfUser2 = friendsDbStorage.getFriends(userId2);

        assertThat(friendsOfUser1).hasSize(1);
        assertThat(friendsOfUser1.iterator().next().getId()).isEqualTo(userId2);

        assertThat(friendsOfUser2).hasSize(1);
        assertThat(friendsOfUser2.iterator().next().getId()).isEqualTo(userId1);
    }

    @Test
    void testRemoveFriendFromMutual() {
        friendsDbStorage.addFriend(userId1, userId2);
        friendsDbStorage.addFriend(userId2, userId1);

        assertThat(friendsDbStorage.getFriends(userId1)).hasSize(1);
        assertThat(friendsDbStorage.getFriends(userId2)).hasSize(1);

        friendsDbStorage.removeFriend(userId1, userId2);

        assertThat(friendsDbStorage.getFriends(userId1)).isEmpty();

        Collection<User> friendsOfUser2 = friendsDbStorage.getFriends(userId2);
        assertThat(friendsOfUser2).hasSize(1);
        assertThat(friendsOfUser2.iterator().next().getId()).isEqualTo(userId1);
    }

    @Test
    void testGetFriendsOrder() {
        friendsDbStorage.addFriend(userId1, userId2);
        friendsDbStorage.addFriend(userId1, userId3);

        Collection<User> friends = friendsDbStorage.getFriends(userId1);

        assertThat(friends)
                .isNotNull()
                .hasSize(2)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(userId2, userId3);
    }

    @Test
    void testAddFriendAfterRemove() {
        friendsDbStorage.addFriend(userId1, userId2);
        assertThat(friendsDbStorage.getFriends(userId1)).hasSize(1);

        friendsDbStorage.removeFriend(userId1, userId2);
        assertThat(friendsDbStorage.getFriends(userId1)).isEmpty();

        friendsDbStorage.addFriend(userId1, userId2);
        assertThat(friendsDbStorage.getFriends(userId1)).hasSize(1);
        assertThat(friendsDbStorage.getFriends(userId1).iterator().next().getId()).isEqualTo(userId2);
    }
}