package ru.practicum.shareit.booking.exceptions;

public class BookingStatusUpdateException extends RuntimeException {
    public BookingStatusUpdateException(final String message) {
        super(message);
    }
}
