package ru.practicum.shareit.booking.exceptions;

public class WrongStateException extends RuntimeException {
    public WrongStateException(String state) {
        super(state);
    }
}
