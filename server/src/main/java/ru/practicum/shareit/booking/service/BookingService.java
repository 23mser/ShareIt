package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingGetDto;

import java.util.List;

public interface BookingService {
    BookingGetDto createBooking(BookingDto bookingDto, Long userId);

    BookingGetDto updateBookingStatus(Long bookingId, Long userId, boolean approved);

    BookingGetDto findBookingById(long bookingId, long userId);

    List<BookingGetDto> findAllBookingsByUser(int from, int size, Long userId, String state);

    List<BookingGetDto> findAllBookingsByOwner(int from, int size, Long userId, String state);
}
