package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.item.dto.ItemShortDto;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
@Getter
public class ItemRequestDto {
    private final Long id;
    private final String description;
    private List<ItemShortDto> items;
    private final LocalDateTime created;
    private final Long requestorId;
}
