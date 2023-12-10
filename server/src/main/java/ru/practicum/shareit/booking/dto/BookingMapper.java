package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {
    public Booking toBooking(BookingDto bookingDto, Item item, User user) {
        return new Booking(bookingDto.getStart(), bookingDto.getEnd(), item, user, BookingStatus.WAITING);
    }

    public BookingGetDto toBookingGetDto(Booking booking) {
        return new BookingGetDto(booking.getId(), booking.getStart(), booking.getEnd(), ItemMapper.toItemDto(booking.getItem()),
                UserMapper.toUserDto(booking.getBooker()), booking.getStatus());
    }

    public static BookingGetItemDto toBookingGetItemDto(Booking booking) {
        if (booking != null) {
            return new BookingGetItemDto(booking.getId(), booking.getBooker().getId(),
                    booking.getStart(), booking.getEnd());
        }
        return null;
    }
}
