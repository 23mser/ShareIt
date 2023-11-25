package ru.practicum.shareit.booking.exceptions;

public class BookingCreateException extends RuntimeException {
    public BookingCreateException(final String message) {
        super(message);
    }
}
