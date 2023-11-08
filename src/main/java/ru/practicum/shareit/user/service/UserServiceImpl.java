package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exceptions.EmailAlreadyExistException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final InMemoryUserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        checkEmail(userDto.getEmail());
        return UserMapper.toUserDto(userRepository.createUser(UserMapper.toUser(userDto)));
    }

    @Override
    public List<UserDto> findAllUsers() {
        return UserMapper.toListUserDto(userRepository.findAllUsers());
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User foundUser = userRepository.findUser(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден."));

        if (userDto.getEmail() != null && !Objects.equals(userDto.getEmail(), foundUser.getEmail())) {
            checkEmail(userDto.getEmail());
            foundUser.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            foundUser.setName(userDto.getName());
        }

        return UserMapper.toUserDto(userRepository.updateUser(foundUser, userId));
    }

    @Override
    public UserDto findUser(Long userId) {
        User user = userRepository.findUser(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден."));

        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteUser(id);
    }

    private void checkEmail(String email) {
        if (userRepository.checkEmail(email)) {
            throw new EmailAlreadyExistException("Почтовый адрес уже занят.");
        }
    }

}
