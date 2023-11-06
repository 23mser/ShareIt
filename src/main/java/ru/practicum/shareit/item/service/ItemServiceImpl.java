package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exceptions.OwnerItemException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.InMemoryItemRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final InMemoryItemRepository itemRepository;
    private final UserServiceImpl userService;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        userService.findUser(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userId);
        return ItemMapper.toItemDto(itemRepository.createItem(item));
    }

    @Override
    public ItemDto findItem(Long id) {
        Item item = itemRepository.findItem(id)
                .orElseThrow(() -> new NoSuchElementException("Вещь не найдена."));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        if (itemDto.getName() == null && itemDto.getDescription() == null && itemDto.getAvailable() == null) {
            throw new NoSuchElementException("Изменения отсутствуют.");
        }

        Item newItem = itemRepository.findItem(itemId)
                .orElseThrow(() -> new NoSuchElementException("Вещь не найдена."));

        if (!(Objects.equals(newItem.getOwner(), userId))) {
            throw new OwnerItemException("Вещь принадлежит другому пользователю.");
        }

        if (itemDto.getName() != null) {
            newItem.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            newItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            newItem.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.updateItem(newItem, itemId));
    }

    @Override
    public List<ItemDto> getAllItemsOfUser(Long userId) {
        userService.findUser(userId);
        return ItemMapper.toItemDtoList(itemRepository.getAllItemsOfUser(userId));
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return ItemMapper.toItemDtoList(itemRepository.searchItems(text));
    }
}
