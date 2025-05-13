package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {
    private final Map<Long, User> usersMap = new HashMap<>();

    public Collection<User> findAll() {
        return usersMap.values();
    }

    public Optional<User> findById(long userId) {
        return Optional.of(usersMap.get(userId));
    }

    public User create(User user) {
        User newUser = new User();
        newUser.setId(getNextId());
        newUser.setName(user.getName());
        newUser.setEmail(user.getEmail());
        usersMap.put(newUser.getId(), newUser);
        return newUser;
    }

    public long getNextId() {
        long currentMaxId = usersMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public User update(long userId, User user) {
        usersMap.put(userId, user);
        return user;
    }

    public void delete(long userId) {
        usersMap.remove(userId);
    }
}
