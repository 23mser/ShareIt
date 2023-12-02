package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exceptions.CommentException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.exceptions.RequestNotFoundException;
import ru.practicum.shareit.request.exceptions.RequestValidateException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest(properties = {
        "spring.config.name=test-application",
        "spring.config.location=classpath:test-application.properties"
}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceTests {
    private final BookingService bookingService;

    private final UserService userService;

    private final ItemService itemService;

    private final UserDto userDto = UserDto.builder()
            .name("name")
            .email("user@email.com")
            .build();

    private final UserDto bookerDto = UserDto.builder()
            .name("booker")
            .email("booker@email.ru")
            .build();

    private final ItemDto itemDto = ItemDto.builder()
            .name("name")
            .description("description")
            .available(true)
            .build();
    private final ItemDto itemDto2 = ItemDto.builder()
            .name("name2")
            .description("description 2")
            .available(true)
            .build();

    @Test
    void createItemTest() {
        UserDto owner = userService.saveUser(userDto);
        User inputUser = UserMapper.toUser(owner);

        ItemDto item = itemService.saveItem(itemDto, owner.getId());
        Item inputItem = ItemMapper.toItem(item, inputUser);

        assertNotNull(inputItem);
        assertNotNull(inputItem.getId());
        assertEquals(inputUser.getId(), inputItem.getOwner().getId());
    }

    @Test
    void createItemTestWithRequestNotFoundExceptionTest() {
        itemDto.setRequestId(5L);
        UserDto owner = userService.saveUser(userDto);

        RequestNotFoundException exception = Assertions.assertThrows(RequestNotFoundException.class, () ->
                itemService.saveItem(itemDto, owner.getId()));

        assertEquals(exception.getMessage(), "Запрос не найден.");
    }

    @Test
    void findAllItemsOfUserTest() {
        UserDto owner = userService.saveUser(userDto);

        itemService.saveItem(itemDto, owner.getId());
        itemService.saveItem(itemDto2, owner.getId());

        Long userId = owner.getId();

        List<ItemDto> itemResponseDtoList = itemService.findAllItemsOfUser(userId, 0, 10);

        assertNotNull(itemResponseDtoList);
        assertEquals(2, itemResponseDtoList.size());
    }

    @Test
    void findAllItemsOfUserWithPaginationErrorTest() {
        UserDto owner = userService.saveUser(userDto);

        Long userId = owner.getId();
        RequestValidateException exception = Assertions.assertThrows(RequestValidateException.class, () ->
                itemService.findAllItemsOfUser(userId, -1, -1));

        assertEquals(exception.getMessage(), "Ошибка пагинации.");
    }

    @Test
    void findItemByIdTest() {
        UserDto owner = userService.saveUser(userDto);

        ItemDto item = itemService.saveItem(itemDto, owner.getId());

        Long itemId = item.getId();
        Long userId = owner.getId();

        ItemDto itemResponseDto = itemService.findItemById(itemId, userId);

        assertNotNull(itemResponseDto);
    }

    @Test
    void updateItemTest() {
        UserDto owner = userService.saveUser(userDto);

        ItemDto item = itemService.saveItem(itemDto, owner.getId());

        Long itemId = item.getId();
        Long userId = owner.getId();

        ItemDto itemResponseDto = itemService.updateItem(item, itemId, userId);

        assertNotNull(itemResponseDto);
    }

    @Test
    void findItemsByRequestTest() {
        UserDto owner = userService.saveUser(userDto);

        itemService.saveItem(itemDto, owner.getId());
        itemService.saveItem(itemDto2, owner.getId());

        Item item3 = new Item();
        item3.setName("Another Item");
        item3.setDescription("test item");
        item3.setAvailable(false);
        ItemDto inputItem = ItemMapper.toItemDto(item3);
        itemService.saveItem(inputItem, owner.getId());

        List<ItemDto> result = itemService.searchItemsByText("Description", 0, 10);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(item -> item.getDescription().equals("description")));
        assertTrue(result.stream().anyMatch(item -> item.getDescription().equals("description 2")));
    }

    @Test
    void searchByTextReturnsEmptyListTest() {
        assertEquals(itemService.searchItemsByText("", 0, 10), Collections.emptyList());
    }

    @Test
    void createCommentTest() {
        UserDto owner = userService.saveUser(userDto);
        UserDto booker = userService.saveUser(bookerDto);

        ItemDto item = itemService.saveItem(itemDto, owner.getId());
        item.setOwner(UserMapper.toUser(owner));

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        bookingService.createBooking(bookingDto, booker.getId());

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test Comment");

        CommentDto commentResponseDto = itemService.createComment(commentDto, item.getId(), booker.getId());

        assertNotNull(commentResponseDto.getId());
        assertEquals(commentDto.getText(), commentResponseDto.getText());
        assertEquals(booker.getName(), commentResponseDto.getAuthorName());
    }

    @Test
    void createCommentWithCommentExceptionTest() {
        UserDto owner = userService.saveUser(userDto);

        UserDto booker = userService.saveUser(bookerDto);
        ItemDto item = itemService.saveItem(itemDto, owner.getId());

        CommentDto commentDto = new CommentDto();
        commentDto.setText("");

        CommentException exception = Assertions.assertThrows(CommentException.class, () ->
                itemService.createComment(commentDto, item.getId(), booker.getId()));

        assertEquals(exception.getMessage(), "Ошибка комментария.");
    }
}
