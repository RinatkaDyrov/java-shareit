package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.Validator;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final Validator validator;

    public Collection<UserDto> findAll() {
        log.debug("Поиск всех пользователей");
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDto findById(long userId) {
        log.debug("Поиск пользователя с ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        return UserMapper.mapToUserDto(user);
    }

    public UserDto create(User user) {
        log.debug("Создание пользователя {}", user);
        if (validator.isEmailExist(user.getEmail())) {
            throw new RuntimeException("Данный имейл уже используется");
        }
        User newUser = userRepository.save(user);
        return UserMapper.mapToUserDto(newUser);
    }

    public UserDto update(long userId, User user) {
        log.debug("Обновление пользователя с ID: {}", userId);
        User updUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        if (user.getName() != null) {
            updUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            if (validator.isEmailExist(user.getEmail())) {
                throw new IllegalStateException("Данный имейл уже используется");
            }
            validator.updateEmail(updUser.getEmail(), user.getEmail());
            updUser.setEmail(user.getEmail());
        }
        userRepository.save(updUser);
        return UserMapper.mapToUserDto(updUser);
    }

    public void deleteUserById(long userId) {
        log.debug("Удаление пользователя с ID: {}", userId);
        User userForDelete = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        validator.removeUser(userForDelete);
        userRepository.delete(userForDelete);
    }
}