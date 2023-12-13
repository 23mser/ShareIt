package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import lombok.Generated;
import ru.practicum.shareit.booking.exceptions.*;
import ru.practicum.shareit.item.exceptions.CommentException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.exceptions.OwnerItemException;
import ru.practicum.shareit.request.exceptions.RequestNotFoundException;
import ru.practicum.shareit.request.exceptions.RequestValidateException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

@Generated
@Slf4j
@RestControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.debug("Ошибка валидации {}", ex.getLocalizedMessage());
        ErrorResponse errorResponse = new ErrorResponse("Ошибка валидации", ex.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException e) {
        log.debug("Недопустимое значение {}", e.getMessage());
        return new ErrorResponse("Недопустимое значение {}", e.getMessage());
    }

    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemNotFoundException(ItemNotFoundException e) {
        log.debug("Недопустимое значение {}", e.getMessage());
        return new ErrorResponse("Недопустимое значение {}", e.getMessage());
    }

    @ExceptionHandler(BookingNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookingNotFoundException(BookingNotFoundException e) {
        log.debug("Недопустимое значение {}", e.getMessage());
        return new ErrorResponse("Недопустимое значение {}", e.getMessage());
    }

    @ExceptionHandler(RequestNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleRequestNotFoundException(RequestNotFoundException e) {
        log.debug("Недопустимое значение {}", e.getMessage());
        return new ErrorResponse("Недопустимое значение {}", e.getMessage());
    }

    @ExceptionHandler(OwnerItemException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleOwnerItemException(OwnerItemException e) {
        log.debug("Отказано в доступе {}", e.getMessage());
        return new ErrorResponse("Отказано в доступе", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(Exception e) {
        log.debug("Непредвиденная ошибка {}", e.getMessage());
        return new ErrorResponse("Непредвиденная ошибка {}", e.getMessage());
    }

    @ExceptionHandler(BookingCreateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingCreateException(BookingCreateException e) {
        return new ErrorResponse("Ошибка бронирования 400: ", e.getMessage());
    }

    @ExceptionHandler(BookingStatusUpdateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingStatusUpdateException(BookingStatusUpdateException e) {
        return new ErrorResponse("Ошибка бронирования 400: ", e.getMessage());
    }

    @ExceptionHandler(UserBookOwnItemException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserBookOwnItemException(UserBookOwnItemException e) {
        return new ErrorResponse("Ошибка бронирования 400: ", e.getMessage());
    }

    @ExceptionHandler(CommentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCommentException(CommentException e) {
        return new ErrorResponse("Невозможно оставить комментарий 400: ", e.getMessage());
    }

    @ExceptionHandler(BookingStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnknownBookingException(BookingStateException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(RequestValidateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRequestValidateException(RequestValidateException e) {
        return new ErrorResponse(e.getMessage());
    }
}
