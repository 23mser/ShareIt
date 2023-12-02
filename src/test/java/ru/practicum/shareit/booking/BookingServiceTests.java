package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingGetDto;
import ru.practicum.shareit.booking.exceptions.BookingCreateException;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.booking.exceptions.BookingStatusUpdateException;
import ru.practicum.shareit.booking.exceptions.UserBookOwnItemException;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.request.exceptions.RequestValidateException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceTests {
    private final BookingService bookingService;

    @Test
    @Order(0)
    @Sql(value = {"/test-schema.sql", "/test-users.sql", "/test-items.sql"})
    @SneakyThrows
    void createBookingTest() {
        BookingDto incomeDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Optional<BookingGetDto> bookingDto = Optional.of(bookingService.createBooking(incomeDto, 2L));

        assertThat(bookingDto)
                .isPresent()
                .hasValueSatisfying(i -> {
                    assertThat(i).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(i).hasFieldOrProperty("item");
                    assertThat(i.getItem()).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(i).hasFieldOrProperty("booker");
                    assertThat(i.getBooker()).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(i).hasFieldOrPropertyWithValue("status", BookingStatus.WAITING);
                });
    }

    @Test
    @Order(1)
    @SneakyThrows
    void updateBookingStatusTest() {
        Optional<BookingGetDto> bookingDto = Optional.of(bookingService.updateBookingStatus(1L, 1L, true));

        assertThat(bookingDto)
                .isPresent()
                .hasValueSatisfying(i -> {
                    assertThat(i).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(i).hasFieldOrProperty("item");
                    assertThat(i.getItem()).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(i).hasFieldOrProperty("booker");
                    assertThat(i.getBooker()).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(i).hasFieldOrPropertyWithValue("status", BookingStatus.APPROVED);
                });
    }

    @Test
    @Order(2)
    @SneakyThrows
    void findBookingByIdTest() {
        Optional<BookingGetDto> bookingDto = Optional.of(bookingService.findBooking(1L, 2L));

        assertThat(bookingDto)
                .isPresent()
                .hasValueSatisfying(i -> {
                    assertThat(i).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(i).hasFieldOrProperty("item");
                    assertThat(i.getItem()).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(i).hasFieldOrProperty("booker");
                    assertThat(i.getBooker()).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(i).hasFieldOrPropertyWithValue("status", BookingStatus.APPROVED);
                });
    }

    @Test
    @Order(3)
    void findBookingByIdWithUserNotFoundExceptionTest() {
        assertThrows(UserNotFoundException.class, () -> bookingService.findBooking(1L, 3L));
    }

    @Test
    @Order(4)
    void updateBookingStatusWithBookingStatusUpdateExceptionTest() {
        assertThrows(BookingStatusUpdateException.class, () -> bookingService.updateBookingStatus(1L, 1L, true));
    }

    @Test
    @Order(5)
    void findBookingByIdWithBookingNotFoundExceptionTest() {
        assertThrows(BookingNotFoundException.class, () -> bookingService.findBooking(10L, 1L));
    }

    @Test
    @Order(6)
    void createBookingWithBookingCreateExceptionTest() {
        BookingDto incomeDto = BookingDto.builder()
                .itemId(2L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThrows(BookingCreateException.class, () -> bookingService.createBooking(incomeDto, 2L));
    }

    @Test
    @Order(7)
    @SneakyThrows
    void updateBookingStatusRejectedTest() {
        BookingDto incomeDto = BookingDto.builder()
                .itemId(3L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        bookingService.createBooking(incomeDto, 2L);

        Optional<BookingGetDto> bookingDto = Optional.of(bookingService.updateBookingStatus(2L, 1L, false));

        assertThat(bookingDto)
                .isPresent()
                .hasValueSatisfying(i -> {
                    assertThat(i).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(i).hasFieldOrProperty("item");
                    assertThat(i.getItem()).hasFieldOrPropertyWithValue("id", 3L);
                    assertThat(i).hasFieldOrProperty("booker");
                    assertThat(i.getBooker()).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(i).hasFieldOrPropertyWithValue("status", BookingStatus.REJECTED);
                });
    }

    @Test
    @Order(8)
    void createBookingWithItemNotFoundExceptionTest() {
        BookingDto incomeDto = BookingDto.builder()
                .itemId(100L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThrows(ItemNotFoundException.class, () -> bookingService.createBooking(incomeDto, 2L));
    }

    @Test
    @Order(9)
    void createBookingWithUserNotFoundExceptionTest() {
        BookingDto incomeDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThrows(UserNotFoundException.class, () -> bookingService.createBooking(incomeDto, 200L));
    }

    @Test
    @Order(10)
    @Sql(value = {"/test-bookings.sql"})
    void confirmNotByOwnerTest() {
        assertThrows(UserBookOwnItemException.class, () -> bookingService.updateBookingStatus(3L, 2L, true));
    }

    @Test
    @Order(11)
    void createBookingWithUserBookOwnItemExceptionTest() {
        BookingDto incomeDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThrows(UserBookOwnItemException.class, () -> bookingService.createBooking(incomeDto, 1L));
    }

    @Test
    @Order(12)
    void findAllBookingsByOwnerTest() {
        int from = 0;
        int size = 10;
        BookingState state = BookingState.ALL;
        long userId = 1L;
        List<BookingGetDto> bookings = bookingService.findAllBookingsByOwner(from, size, userId, String.valueOf(state));
        Assertions.assertThat(bookings).hasSize(5);

        state = BookingState.CURRENT;
        bookings = bookingService.findAllBookingsByOwner(from, size, userId, String.valueOf(state));
        Assertions.assertThat(bookings).isEmpty();

        state = BookingState.PAST;
        bookings = bookingService.findAllBookingsByOwner(from, size, userId, String.valueOf(state));
        Assertions.assertThat(bookings).hasSize(1);

        state = BookingState.FUTURE;
        bookings = bookingService.findAllBookingsByOwner(from, size, userId, String.valueOf(state));
        Assertions.assertThat(bookings).hasSize(4);

        state = BookingState.WAITING;
        bookings = bookingService.findAllBookingsByOwner(from, size, userId, String.valueOf(state));
        Assertions.assertThat(bookings).hasSize(1);

        state = BookingState.REJECTED;
        bookings = bookingService.findAllBookingsByOwner(from, size, userId, String.valueOf(state));
        Assertions.assertThat(bookings).hasSize(2);
    }

    @Test
    @Order(12)
    void findAllBookingsByUserWithRequestValidateExceptionTest() {
        int from = 0;
        int size = 10;
        BookingState state = BookingState.ALL;
        long userId = 2L;
        List<BookingGetDto> bookings = bookingService.findAllBookingsByUser(from, size, userId, String.valueOf(state));
        Assertions.assertThat(bookings).hasSize(5);

        state = BookingState.CURRENT;
        bookings = bookingService.findAllBookingsByUser(from, size, userId, String.valueOf(state));
        Assertions.assertThat(bookings).isEmpty();

        state = BookingState.PAST;
        bookings = bookingService.findAllBookingsByUser(from, size, userId, String.valueOf(state));
        Assertions.assertThat(bookings).hasSize(1);

        state = BookingState.FUTURE;
        bookings = bookingService.findAllBookingsByUser(from, size, userId, String.valueOf(state));
        Assertions.assertThat(bookings).hasSize(4);

        state = BookingState.WAITING;
        bookings = bookingService.findAllBookingsByUser(from, size, userId, String.valueOf(state));
        Assertions.assertThat(bookings).hasSize(1);

        state = BookingState.REJECTED;
        bookings = bookingService.findAllBookingsByUser(from, size, userId, String.valueOf(state));
        Assertions.assertThat(bookings).hasSize(2);
    }

    @Test
    @Order(13)
    @Sql(value = {"/test-schema.sql", "/test-users.sql", "/test-items.sql"})
    @SneakyThrows
    void createBookingBookingCreateExceptionTest() {
        BookingDto incomeDto = BookingDto.builder()
                .itemId(1L)
                .start(null)
                .end(null)
                .build();

        BookingCreateException exception = assertThrows(BookingCreateException.class, () ->
                bookingService.createBooking(incomeDto, 2L));

        assertEquals(exception.getMessage(), "Ошибка создания бронирования.");
    }

    @Test
    @Order(14)
    void findAllBookingsByOwnerWithRequestValidateExceptionTest() {
        int from = -1;
        int size = -1;
        BookingState state = BookingState.ALL;
        long userId = 1L;

        RequestValidateException exception = assertThrows(RequestValidateException.class, () ->
                bookingService.findAllBookingsByOwner(from, size, userId, String.valueOf(state)));

        assertEquals(exception.getMessage(), "Ошибка пагинации.");
    }

    @Test
    @Order(15)
    void findAllBookingsByUserWithPaginationErrorTest() {
        int from = -1;
        int size = -1;
        BookingState state = BookingState.ALL;
        long userId = 2L;

        RequestValidateException exception = assertThrows(RequestValidateException.class, () ->
                bookingService.findAllBookingsByUser(from, size, userId, String.valueOf(state)));

        assertEquals(exception.getMessage(), "Ошибка пагинации.");
    }
}
