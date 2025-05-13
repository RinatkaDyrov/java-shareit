package ru.practicum.shareit.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class Validator {
    private final Set<Long> existingUsers;
    private final Set<Long> existingItems;

    public void itemValidation(Long itemId) {
        boolean valid = existingItems.add(itemId);
        if (!valid) {
            throw new NullPointerException("Вещь с таким ID уже существует");
        }
    }

    public void userValidation(Long userId) {
        boolean valid = existingUsers.add(userId);
        if (!valid) {
            throw new NullPointerException("Пользователь с таким ID уже существует");
        }
    }

    public void removeUser(Long userId) {
        existingUsers.remove(userId);
    }

    public void removeItem(Long itemId) {
        existingItems.remove(itemId);
    }
}
