package ru.practicum.shareit.booking.exceptions;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(final String message) {
        super(message);
    }
}
