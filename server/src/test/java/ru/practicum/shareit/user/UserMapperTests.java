package ru.practicum.shareit.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class UserMapperTests {
    @Test
    void toUserTest() {
        UserDto incomeDto = UserDto.builder()
                .name("userName")
                .email("user@mail.com")
                .build();

        User user = UserMapper.toUser(incomeDto);

        Assertions.assertThat(user)
                .hasFieldOrPropertyWithValue("name", "userName")
                .hasFieldOrPropertyWithValue("email", "user@mail.com");
    }

    @Test
    void toUserDtoTest() {
        User user = fillUser();

        UserDto userDto = UserMapper.toUserDto(user);

        Assertions.assertThat(userDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "name")
                .hasFieldOrPropertyWithValue("email", "user@mail.com");
    }

    @Test
    void toUsersDtoTest() {
        List<User> users = List.of(fillUser());

        List<UserDto> usersDto = UserMapper.toListUserDto(users);

        Assertions.assertThat(usersDto)
                .hasSize(1);
    }

    private User fillUser() {
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("user@mail.com");

        return user;
    }
}
