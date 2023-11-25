package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exceptions.BookingCreateException;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.booking.exceptions.BookingStateException;
import ru.practicum.shareit.booking.exceptions.BookingStatusUpdateException;
import ru.practicum.shareit.booking.exceptions.UserBookOwnItemException;
import ru.practicum.shareit.booking.exceptions.WrongStateException;

@RestControllerAdvice("ru.practicum.shareit.booking")
@Slf4j
public class ErrorBookingHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorBookingResponse handlerBookingCreateException(final BookingCreateException e) {
        log.warn("Booking create exception: ", e);
        return new ErrorBookingResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorBookingResponse handlerBookingStatusUpdateException(final BookingStatusUpdateException e) {
        log.warn("Booking status update exception: ", e);
        return new ErrorBookingResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorBookingResponse handlerBookingExistsException(final BookingNotFoundException e) {
        log.warn("Booking exists exception: ", e);
        return new ErrorBookingResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorBookingResponse handlerUserBookOwnItemException(final UserBookOwnItemException e) {
        log.warn("User book own item exception: ", e);
        return new ErrorBookingResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorBookingResponse handlerBookingStateException(final BookingStateException e) {
        log.warn("Booking state exception: ", e);
        return new ErrorBookingResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorBookingResponse handlerWrongStateException(final WrongStateException e) {
        log.warn("Wrong state exception: ", e);
        return new ErrorBookingResponse(e.getMessage());
    }
}
