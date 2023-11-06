package ru.practicum.shareit.user.exceptions;

public class EmailAlreadyExistException extends RuntimeException {
    public EmailAlreadyExistException(final String message) {
        super(message);
    }

}
