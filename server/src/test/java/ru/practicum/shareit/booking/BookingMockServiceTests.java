package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingGetDto;
import ru.practicum.shareit.booking.exceptions.BookingCreateException;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.booking.exceptions.BookingStateException;
import ru.practicum.shareit.booking.exceptions.BookingStatusUpdateException;
import ru.practicum.shareit.booking.exceptions.UserBookOwnItemException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingMockServiceTests {
    public static final LocalDateTime DATE = LocalDateTime.now();

    private BookingService bookingService;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private BookingRepository bookingRepository;

    private User user;
    private Item item;
    private User owner;
    private Booking booking;
    private BookingDto inputDto;

    @BeforeEach
    public void start() {
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);

        inputDto = BookingDto.builder()
                .itemId(5L)
                .start(DATE)
                .end(DATE.plusDays(10))
                .build();

        user = User.builder()
                .id(2L)
                .name("name")
                .email("user@email.com")
                .build();

        owner = User.builder()
                .id(3L)
                .name("owner")
                .email("user2@email.ru")
                .build();

        item = Item.builder()
                .id(5L)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(DATE)
                .end(DATE.plusDays(10))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    void createBookingTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);

        BookingGetDto result = bookingService.createBooking(inputDto, 2L);

        assertNotNull(result);
        assertEquals(inputDto.getItemId(), result.getItem().getId());
        assertEquals(inputDto.getStart(), result.getStart());
        assertEquals(inputDto.getEnd(), result.getEnd());
    }

    @Test
    void createBookingWithBookingCreateExceptionTest() {
        item.setAvailable(false);

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        BookingCreateException exception = assertThrows(BookingCreateException.class, () ->
                bookingService.createBooking(inputDto, 2L));

        assertNotNull(exception);
    }

    @Test
    void createBookingWithUserBookOwnItemExceptionTest() {
        item.setOwner(user);

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        UserBookOwnItemException exception = assertThrows(UserBookOwnItemException.class, () ->
                bookingService.createBooking(inputDto, 2L));

        assertNotNull(exception);
    }

    @Test
    void updateBookingTest() {
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));

        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);

        BookingGetDto result = bookingService.updateBookingStatus(1L, 3L, true);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void updateBookingWithBookingNotFoundExceptionTest() {
        booking.setStatus(BookingStatus.WAITING);
        item.setOwner(user);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class, () ->
                bookingService.updateBookingStatus(1L, 2L, true));

        assertNotNull(exception);
    }

    @Test
    void updateBookingWithBookingCreateExceptionTest() {
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));

        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);

        booking.setStatus(BookingStatus.APPROVED);

        BookingStatusUpdateException exception = assertThrows(BookingStatusUpdateException.class, () ->
                bookingService.updateBookingStatus(1L, 3L, true));

        assertNotNull(exception);
    }

    @Test
    void findBookingByIdTest() {
        item.setOwner(owner);

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));

        BookingGetDto result = bookingService.findBookingById(1L, 2L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findBookingByIdWithBookingNotFoundExceptionTest() {
        user.setId(11L);

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));

        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class, () ->
                bookingService.findBookingById(1L, 1L));

        assertNotNull(exception);
    }

    @Test
    void findAllBookingsByUserWithStateRejectedTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        BookingStateException exception = assertThrows(BookingStateException.class, () ->
                bookingService.findAllBookingsByUser(0, 10, 2L, "rejected"));

        assertNotNull(exception);
    }

    @Test
    void findAllBookingsByUserStateWaitingTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        BookingStateException exception = assertThrows(BookingStateException.class, () ->
                bookingService.findAllBookingsByUser(0, 10, 2L, "waiting"));

        assertNotNull(exception);
    }

    @Test
    void findAllBookingsByUserStateCurrentTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        BookingStateException exception = assertThrows(BookingStateException.class, () ->
                bookingService.findAllBookingsByUser(0, 10, 2L, "current"));

        assertNotNull(exception);
    }

    @Test
    void findAllBookingsByUserStateFutureTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        BookingStateException exception = assertThrows(BookingStateException.class, () ->
                bookingService.findAllBookingsByUser(0, 10, 2L, "future"));

        assertNotNull(exception);
    }

    @Test
    void findAllBookingsByUserStatePastTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        BookingStateException exception = assertThrows(BookingStateException.class, () ->
                bookingService.findAllBookingsByUser(0, 10, 2L, "past"));

        assertNotNull(exception);
    }

    @Test
    void findAllBookingsByUserStateAllTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(bookingRepository.findByBookerIdOrderByStartDesc(any(), any())).thenReturn((List.of(booking)));

        List<BookingGetDto> result = bookingService.findAllBookingsByUser(0, 10, 2L, "ALL");

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void findAllBookingsByOwnerStateRejectedTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(Collections.singletonList(item));

        when(bookingRepository.findBookingByItemOwnerAndStatus(any(), any(), any())).thenReturn(Collections.singletonList(booking));

        List<BookingGetDto> result = bookingService.findAllBookingsByOwner(0, 10, 3L, "REJECTED");

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void findAllBookingsByOwnerStateWaitingTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(Collections.singletonList(item));

        when(bookingRepository.findBookingByItemOwnerAndStatus(any(), any(), any())).thenReturn(Collections.singletonList(booking));

        List<BookingGetDto> result = bookingService.findAllBookingsByOwner(0, 10, 3L, "WAITING");

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void findAllBookingsByOwnerStateCurrentTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(Collections.singletonList(item));

        when(bookingRepository.findBookingsByItemOwnerCurrent(any(), any())).thenReturn(Collections.singletonList(booking));

        List<BookingGetDto> result = bookingService.findAllBookingsByOwner(0, 10, 3L, "CURRENT");

        assertNotNull(result);
    }

    @Test
    void findAllBookingsByOwnerStateFutureTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(Collections.singletonList(item));

        when(bookingRepository.findBookingByItemOwnerAndStartIsAfter(any(), any(), any())).thenReturn(Collections.singletonList(booking));

        List<BookingGetDto> result = bookingService.findAllBookingsByOwner(0, 10, 3L, "FUTURE");

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void findAllBookingsByOwnerStatePastTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(Collections.singletonList(item));

        when(bookingRepository.findBookingByItemOwnerAndEndIsBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(Collections.singletonList(booking));

        List<BookingGetDto> result = bookingService.findAllBookingsByOwner(0, 10, 3L, "PAST");

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void findAllBookingsByOwnerStateAllTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(Collections.singletonList(item));

        when(bookingRepository.findAllBookingsByOwner(anyLong(), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingGetDto> result = bookingService.findAllBookingsByOwner(0, 10, 3L, "ALL");

        assertNotNull(result);
    }
}
