package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto saveItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.saveItem(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItem(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findItemById(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping
    public List<ItemDto> findAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findAllItemsOfUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByText(@RequestParam String text) {
        return itemService.searchItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto comment, @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId) {
        return itemService.createComment(comment, itemId, userId);
    }
}
