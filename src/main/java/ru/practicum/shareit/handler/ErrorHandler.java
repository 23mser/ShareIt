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
    public ResponseEntity<Object> handleUserNotFoundException(final UserNotFoundException e) {
        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.put("status", HttpStatus.NOT_FOUND.name());
        response.put("message", e.getMessage());

        log.warn("User not found exception: ", e);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(value = ItemNotFoundException.class)
    public ResponseEntity<Object> handleItemNotFoundException(final ItemNotFoundException e) {
        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.put("status", HttpStatus.NOT_FOUND.name());
        response.put("message", e.getMessage());

        log.warn("Item not found exception: ", e);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(value = OwnerItemException.class)
    public ResponseEntity<Object> handleOwnerItemException(final OwnerItemException e) {
        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.put("status", HttpStatus.FORBIDDEN.name());
        response.put("message", e.getMessage());

        log.warn("Owner item exception: ", e);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(value = EmailAlreadyExistException.class)
    public ResponseEntity<Object> handleEmailAlreadyExistException(final EmailAlreadyExistException e) {
        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.put("status", HttpStatus.CONFLICT.name());
        response.put("message", e.getMessage());

        log.warn("Email already exist exception: ", e);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(value = CommentException.class)
    public ResponseEntity<Object> handleCommentException(final CommentException e) {
        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.put("status", HttpStatus.BAD_REQUEST.name());
        response.put("message", e.getMessage());

        log.warn("Comment exception: ", e);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
