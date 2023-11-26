package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingGetDto;

import java.util.List;

public interface BookingService {
    BookingGetDto createBooking(BookingDto bookingDto, Long userId);

    BookingGetDto updateBookingStatus(Long bookingId, Long userId, boolean approved);

    BookingGetDto getBooking(Long bookingId, Long userId);

    List<BookingGetDto> getAllBookingsByUser(String state, Long userId);

    List<BookingGetDto> getAllBookingsByOwner(String state, Long userId);
}
