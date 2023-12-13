package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.dto.CommentDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerTests {
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;
    @MockBean
    private final ItemService itemService;

    @SneakyThrows
    @Test
    void findItemByIdTest() {
        long itemId = 1L;
        long userId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(itemService).findItemById(itemId, userId);
    }

    @SneakyThrows
    @Test
    void findItemByIdBadRequestTest() {
        long itemId = 1L;
        long userId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", itemId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(itemService, Mockito.never()).findItemById(itemId, userId);
    }

    @SneakyThrows
    @Test
    void findAllItemsOfUserWithoutPaginationTest() {
        long userId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(itemService).findAllItemsOfUser(userId, 0, 10);
    }

    @SneakyThrows
    @Test
    void findAllItemsOfUserWithPaginationTest() {
        long userId = 1L;
        int from = 3;
        int size = 2;
        mockMvc.perform(MockMvcRequestBuilders.get("/items?from={from}&size={size}", from, size)
                        .header("X-Sharer-User-Id", userId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(itemService).findAllItemsOfUser(userId, 3, 2);
    }

    @SneakyThrows
    @Test
    void searchByTextWithoutPaginationParams() {
        String text = "java forever";
        mockMvc.perform(MockMvcRequestBuilders.get("/items/search?text={text}", text))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(itemService).searchItemsByText(text, 0, 10);
    }

    @SneakyThrows
    @Test
    void saveItemTest() {
        long userId = 1L;
        ItemDto itemIncomeDto = ItemDto.builder()
                .name("")
                .description("")
                .available(false)
                .requestId(null)
                .build();
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("")
                .description("")
                .available(false)
                .requestId(null)
                .build();

        when(itemService.saveItem(any(), anyLong())).thenReturn(itemDto);
        String result = mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemIncomeDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @SneakyThrows
    @Test
    void updateItemTest() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto incomeDto = ItemDto.builder()
                .name("item")
                .description("item description")
                .available(true)
                .requestId(null)
                .build();
        ItemDto itemDto = ItemDto.builder()
                .name("item")
                .description("item description")
                .available(true)
                .owner(User.builder()
                        .id(1L)
                        .name("user")
                        .email("user@yandex.ru")
                        .build())
                .nextBooking(null)
                .lastBooking(null)
                .comments(List.of())
                .requestId(null)
                .build();

        Mockito.when(itemService.updateItem(ArgumentMatchers.any(), ArgumentMatchers.anyLong(),
                ArgumentMatchers.anyLong())).thenReturn(itemDto);
        String content = mockMvc.perform(MockMvcRequestBuilders.patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomeDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(itemDto), content);
    }

    @SneakyThrows
    @Test
    void createCommentTest() {
        Long userId = 1L;
        Long itemId = 1L;
        CommentDto commentDto = CommentDto.builder()
                .authorName("user")
                .text("not bad")
                .build();

        Mockito.when(itemService.createComment(ArgumentMatchers.any(), ArgumentMatchers.anyLong(),
                ArgumentMatchers.anyLong())).thenReturn(commentDto);
        String content = mockMvc.perform(MockMvcRequestBuilders.post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(commentDto), content);
    }
}
