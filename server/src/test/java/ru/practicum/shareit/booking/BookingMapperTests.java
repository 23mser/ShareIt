package ru.practicum.shareit.booking;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingGetDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class BookingMapperTests {
    @Test
    void toBookingTest() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);

        BookingDto bookingIncomeDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();

        Booking booking = BookingMapper.toBooking(bookingIncomeDto, new Item(), new User());

        Assertions.assertThat(booking)
                .hasFieldOrPropertyWithValue("id", 0L)
                .hasFieldOrPropertyWithValue("start", start)
                .hasFieldOrPropertyWithValue("end", end);
    }

    @Test
    void toBookingDtoTest() {
        Booking booking = fillBooking();

        BookingGetDto bookingDto = BookingMapper.toBookingGetDto(booking);

        Assertions.assertThat(bookingDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", booking.getStart())
                .hasFieldOrPropertyWithValue("end", booking.getEnd())
                .hasFieldOrProperty("item")
                .hasFieldOrProperty("booker")
                .hasFieldOrPropertyWithValue("status", BookingStatus.APPROVED);
        Assertions.assertThat(bookingDto.getItem())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "item_name");
        Assertions.assertThat(bookingDto.getBooker())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "user_name");
    }

    private Booking fillBooking() {
        Item item = new Item();
        item.setId(1L);
        item.setName("item_name");

        User user = new User();
        user.setId(1L);
        user.setName("user_name");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setItem(item);
        booking.setBooker(user);

        return booking;
    }
}
