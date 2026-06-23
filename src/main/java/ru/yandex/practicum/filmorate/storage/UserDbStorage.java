package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

@Qualifier
@Repository("userDbStorage")
public class UserDbStorage extends BaseStorage<User> implements UserStorage {
    private static final String GET_USERS = "SELECT * FROM users";
    private static final String CREATE_USER = "INSERT INTO users(user_name, email, login, birthday) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE users  SET user_name = ?, email = ?, login = ?," +
            " birthday = ? WHERE user_id = ?";
    private static final String FIND_BY_ID = "SELECT * FROM users WHERE user_id = ?";

    public UserDbStorage(JdbcTemplate jdbc, UserRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<User> getUsers() {
        return findMany(GET_USERS);
    }

    @Override
    public User createUser(User user) {
        long id = insert(
                CREATE_USER,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        if (findById(newUser.getId()).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        boolean updated = update(
                UPDATE_USER,
                newUser.getName(),
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getBirthday(),
                newUser.getId()
        );

        if (!updated) {
            throw new InternalServerException("Не удалось обновить пользователя");
        }

        return newUser;
    }

    @Override
    public Optional<User> findById(long id) {
        return findOne(FIND_BY_ID, id);
    }

}
