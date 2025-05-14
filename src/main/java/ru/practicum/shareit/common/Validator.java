package ru.practicum.shareit.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class Validator {
    private final Set<Long> existingUsers;
    private final Set<Long> existingItems;
    private final Set<String> existingEmails;

    public void trackNewItem(Long itemId) {
        if (isItemExist(itemId)) {
            throw new IllegalStateException("Вещь с таким ID уже существует");
        }
        existingItems.add(itemId);
    }

    public void trackNewUser(User user) {
        if (isUserExist(user.getId())) {
            throw new IllegalStateException("Пользователь с таким ID уже существует");
        }
        if (isEmailExist(user.getEmail())) {
            throw new IllegalStateException("Пользователь с таким имейлом уже существует");
        }
        existingUsers.add(user.getId());
        existingEmails.add(user.getEmail());
    }


    public boolean isEmailExist(String email) {
        return existingEmails.contains(email);
    }

    public boolean isUserExist(Long userId) {
        return existingUsers.contains(userId);
    }

    public boolean isItemExist(Long itemId) {
        return existingItems.contains(itemId);
    }

    public void removeUser(User user) {
        existingUsers.remove(user.getId());
        existingEmails.remove(user.getEmail());

    }

    public void removeItem(Long itemId) {
        existingItems.remove(itemId);
    }

    public void updateEmail(String oldEmail, String newEmail) {
        existingEmails.remove(oldEmail);
        existingEmails.add(newEmail);
    }
}
