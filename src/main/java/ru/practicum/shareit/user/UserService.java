package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Collection<UserDto> findAll() {
        return userRepository.findAll();
    }


    public UserDto findById(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->new NotFoundException("Пользователь с id " + userId + " не найден"));
        return UserMapper.mapToUserDto(user);
    }

    public UserDto create(User user) {
    }

    public UserDto update(long userId, User updUser) {
    }

    public void deleteUserById(long userId) {
    }
}
