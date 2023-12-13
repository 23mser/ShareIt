package ru.practicum.shareit.booking.exceptions;

public class UserBookOwnItemException extends RuntimeException {
    public UserBookOwnItemException(final String message) {
        super(message);
    }
}
