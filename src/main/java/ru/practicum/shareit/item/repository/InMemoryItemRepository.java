package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 1L;

    public Item createItem(Item item) {
        item.setId(id++);
        items.put(item.getId(), item);
        return item;
    }

    public Optional<Item> findItem(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    public Item updateItem(Item item, Long itemId) {
        items.put(itemId, item);
        return item;
    }

    public List<Item> getAllItemsOfUser(Long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Item> searchItems(String text) {
        return items.values()
                .stream()
                .filter(item -> item.getAvailable().equals(true) &&
                        (item.getDescription().toLowerCase().contains(text.toLowerCase()) ||
                                item.getName().toLowerCase().contains(text.toLowerCase()))
                )
                .collect(Collectors.toList());
    }
}
