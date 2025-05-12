package ru.practicum.shareit.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.common.BaseRepository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<User> {

    private static final String FIND_ALL = "SELECT * FROM users";
    private static final String FIND_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users(name, email)" +
            "VALUES (?, ?)";

    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public Collection<User> findAll() {
        return findMany(FIND_ALL);
    }

    public Optional<User> findById(long userId) {
        return findOne(FIND_BY_ID, userId);
    }

    public User create(User user) {
        long id = insert(INSERT_QUERY, user.getName(), user.getEmail());
        user.setId(id);
        return user;
    }
}
