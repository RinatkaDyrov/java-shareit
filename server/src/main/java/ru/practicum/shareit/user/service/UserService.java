package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    Collection<UserDto> findAll();

    UserDto findById(long userId);

    UserDto create(User user);

    UserDto update(long userId, User user);

    void deleteUserById(long userId);
}
