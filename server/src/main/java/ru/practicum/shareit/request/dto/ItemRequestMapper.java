package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDto requestDto) {
        return ItemRequest.builder()
                .id(requestDto.getId())
                .description(requestDto.getDescription())
                .created(requestDto.getCreated())
                .requestorId(requestDto.getRequestorId())
                .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest request) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requestorId(request.getRequestorId())
                .created(request.getCreated())
                .build();
    }

    public static ItemRequestLongDto.ShortItemResponseDto toShortItemResponseDto(Item item) {
        return ItemRequestLongDto.ShortItemResponseDto.builder()
                .itemId(item.getId())
                .itemName(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getItemRequest().getId())
                .build();
    }

    public static ItemRequestLongDto toItemRequestDtoForOwner(ItemRequest itemRequest, List<Item> items) {
        return ItemRequestLongDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items.stream().map(ItemRequestMapper::toShortItemResponseDto).collect(Collectors.toList()))
                .build();
    }

    public static List<ItemRequestLongDto> toListItemRequestDtoForOwner(List<ItemRequest> itemRequests,
                                                                        Map<ItemRequest, List<Item>> itemsByRequests) {
        return itemRequests.stream()
                .map(itemRequest -> toItemRequestDtoForOwner(itemRequest, itemsByRequests.getOrDefault(itemRequest, List.of())))
                .collect(Collectors.toList());
    }
}
