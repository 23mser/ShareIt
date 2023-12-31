package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAllUsers();

    UserDto saveUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, Long userId);

    UserDto findUserById(Long userId);

    void deleteUserById(Long userId);
}
