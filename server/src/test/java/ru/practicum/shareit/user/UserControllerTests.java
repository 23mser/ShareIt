package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTests {
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;
    @MockBean
    private final UserService userService;

    @SneakyThrows
    @Test
    void findUserByIdTest() {
        long userId = 1L;
        mockMvc.perform(get("/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).findUserById(userId);
    }

    @SneakyThrows
    @Test
    void findAllUsersTest() {
        long userId = 1L;
        mockMvc.perform(get("/users", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).findAllUsers();
    }

    @SneakyThrows
    @Test
    void saveUserTest() {
        UserDto userIncomeDto = UserDto.builder()
                .name("User")
                .email("user@yandex.ru")
                .build();
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@yandex.ru")
                .build();

        when(userService.saveUser(any())).thenReturn(userDto);
        String result = mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userIncomeDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @SneakyThrows
    @Test
    void updateUserTest() {
        UserDto userIncomeDto = UserDto.builder()
                .name("User")
                .email("user@yandex.ru")
                .build();
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("UserUpdate")
                .email("user@yandex.ru")
                .build();

        when(userService.updateUser(any(), anyLong())).thenReturn(userDto);
        String result = mockMvc.perform(
                        patch("/users/{userId}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userIncomeDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @SneakyThrows
    @Test
    void deleteUserByIdTest() {
        long userId = 1L;
        mockMvc.perform(delete("/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).deleteUserById(userId);
    }
}
