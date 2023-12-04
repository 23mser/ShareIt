package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(ItemDto itemDto, Long userId);

    ItemDto findItemById(Long id, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long id, Long userId);

    List<ItemDto> findAllItemsOfUser(Long userId, int from, int size);

    List<ItemDto> searchItemsByText(String text, int from, int size);

    CommentDto createComment(CommentDto dto, Long itemId, Long userId);
}