package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTests {
    private final UserService userService;

    @Test
    @Order(0)
    @Sql(value = {"/test-schema.sql"})
    void saveUserTest() {
        UserDto userCreateDto = UserDto.builder()
                .name("user")
                .email("user@yandex.ru")
                .build();
        Optional<UserDto> userDto = Optional.of(userService.saveUser(userCreateDto));

        assertThat(userDto)
                .isPresent()
                .hasValueSatisfying(f -> {
                            assertThat(f).hasFieldOrPropertyWithValue("id", 1L);
                            assertThat(f).hasFieldOrPropertyWithValue("name", "user");
                            assertThat(f).hasFieldOrPropertyWithValue("email", "user@yandex.ru");
                        }
                );
    }

    @Test
    @Order(1)
    void updateUserTest() {
        UserDto userUpdateDto = UserDto.builder()
                .name("userUpdated")
                .email("userUpdated@yandex.ru")
                .build();
        Optional<UserDto> userDto = Optional.of(userService.updateUser(userUpdateDto, 1L));

        assertThat(userDto)
                .isPresent()
                .hasValueSatisfying(f -> {
                            assertThat(f).hasFieldOrPropertyWithValue("id", 1L);
                            assertThat(f).hasFieldOrPropertyWithValue("name", "userUpdated");
                            assertThat(f).hasFieldOrPropertyWithValue("email", "userUpdated@yandex.ru");
                        }
                );
    }

    @Test
    @Order(2)
    void findUserByIdTest() {
        Optional<UserDto> userDto = Optional.of(userService.findUserById(1L));

        assertThat(userDto)
                .isPresent()
                .hasValueSatisfying(f -> {
                            assertThat(f).hasFieldOrPropertyWithValue("id", 1L);
                            assertThat(f).hasFieldOrPropertyWithValue("name", "userUpdated");
                            assertThat(f).hasFieldOrPropertyWithValue("email", "userUpdated@yandex.ru");
                        }
                );
    }

    @Test
    @Order(3)
    void findUserByIdWithUserNotFoundExceptionTest() {
        assertThrows(UserNotFoundException.class, () -> userService.findUserById(100L));
    }

    @Test
    @Order(4)
    void findAllUsersTest() {
        UserDto userCreateDto = UserDto.builder()
                .name("user1")
                .email("user1@yandex.ru")
                .build();
        userService.saveUser(userCreateDto);
        List<UserDto> users = userService.findAllUsers();

        assertThat(users)
                .hasSize(2)
                .map(UserDto::getId)
                .contains(1L, 2L);
    }

    @Test
    @Order(5)
    void deleteUserByIdTest() {
        userService.deleteUserById(1L);
        List<UserDto> users = userService.findAllUsers();

        assertThat(users)
                .hasSize(1)
                .map(UserDto::getId)
                .contains(2L);
    }

}
