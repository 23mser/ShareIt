package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.practicum.shareit.item.exceptions.CommentException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.exceptions.OwnerItemException;
import ru.practicum.shareit.request.exceptions.RequestNotFoundException;
import ru.practicum.shareit.request.exceptions.RequestValidateException;
import ru.practicum.shareit.user.exceptions.EmailAlreadyExistException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(final UserNotFoundException ex) {
        Map<String, Object> response = getResponse(HttpStatus.NOT_FOUND, ex);
        log.warn("User not found exception: ", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(value = ItemNotFoundException.class)
    public ResponseEntity<Object> handleItemNotFoundException(final ItemNotFoundException ex) {
        Map<String, Object> response = getResponse(HttpStatus.NOT_FOUND, ex);
        log.warn("Item not found exception: ", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(value = OwnerItemException.class)
    public ResponseEntity<Object> handleOwnerItemException(final OwnerItemException ex) {
        Map<String, Object> response = getResponse(HttpStatus.FORBIDDEN, ex);
        log.warn("Owner item exception: ", ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(value = EmailAlreadyExistException.class)
    public ResponseEntity<Object> handleEmailAlreadyExistException(final EmailAlreadyExistException ex) {
        Map<String, Object> response = getResponse(HttpStatus.CONFLICT, ex);
        log.warn("Email already exist exception: ", ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(value = CommentException.class)
    public ResponseEntity<Object> handleCommentException(final CommentException ex) {
        Map<String, Object> response = getResponse(HttpStatus.BAD_REQUEST, ex);
        log.warn("Comment exception: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(value = RequestNotFoundException.class)
    public ResponseEntity<Object> handleRequestNotFoundException(final RequestNotFoundException ex) {
        Map<String, Object> response = getResponse(HttpStatus.NOT_FOUND, ex);
        log.warn("Request not found exception: ", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(value = RequestValidateException.class)
    public ResponseEntity<Object> handleRequestValidateException(final RequestValidateException ex) {
        Map<String, Object> response = getResponse(HttpStatus.BAD_REQUEST, ex);
        log.error("Request validate exception: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private Map<String, Object> getResponse(HttpStatus httpStatus, Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.put("status", httpStatus.name());
        response.put("message", ex.getMessage());
        return response;
    }
}
