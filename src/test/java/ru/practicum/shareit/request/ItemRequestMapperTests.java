package ru.practicum.shareit.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestIncomeDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class ItemRequestMapperTests {
    @Test
    void toBookingTest() {
        ItemRequestIncomeDto itemRequestDto = ItemRequestIncomeDto.builder()
                .description("des")
                .build();

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, new User());

        Assertions.assertThat(itemRequest)
                .hasFieldOrPropertyWithValue("description", "des");
    }

    @Test
    void toBookingDtoTest() {
        ItemRequest itemRequest = fillItemRequest();

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);

        Assertions.assertThat(itemRequestDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("description", itemRequest.getDescription())
                .hasFieldOrPropertyWithValue("created", itemRequest.getCreated());
    }

    private ItemRequest fillItemRequest() {
        User user = new User();
        user.setId(1L);
        user.setName("user_name");

        LocalDateTime time = LocalDateTime.now();

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("des");
        itemRequest.setCreated(time);
        itemRequest.setRequestor(user);

        return itemRequest;
    }
}
