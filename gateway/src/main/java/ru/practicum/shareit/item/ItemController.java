package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Validated
public class ItemController {

    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> saveItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @RequestBody @Valid ItemDto dto) {
        return itemClient.saveItem(dto, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findItemById(@RequestHeader(USER_ID_HEADER) @Positive Long userId,
                                               @PathVariable("id") @Positive Long itemId) {
        return itemClient.findItemById(itemId, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @PathVariable("id") Long itemId,
                                             @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllItemsOfUser(@RequestHeader(USER_ID_HEADER) Long userId,
                                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        return itemClient.findAllItemsOfUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByText(@RequestParam("text") String text,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        if (!StringUtils.hasText(text)) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return itemClient.searchItemsByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @PathVariable("itemId") @Positive Long itemId,
                                             @Valid @RequestBody CommentDto comment) {
        return itemClient.createComment(itemId, userId, comment);
    }
}
