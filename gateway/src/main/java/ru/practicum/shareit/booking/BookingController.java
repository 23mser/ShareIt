package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.handler.BookingStateException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                                @RequestBody @Valid BookingGetDto requestDto) {
        return bookingClient.createBooking(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(@RequestHeader(USER_ID_HEADER) Long userId,
                                                @PathVariable("bookingId") Long bookingId,
                                                @RequestParam("approved") Boolean approved) {
        return bookingClient.updateBookingStatus(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findBookingById(@RequestHeader(USER_ID_HEADER) Long userId,
                                                  @PathVariable Long bookingId) {
        return bookingClient.findBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllBookingsByUser(@RequestHeader(USER_ID_HEADER) Long userId,
                                                     @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        BookingState bookingState = BookingState.from(stateParam)
                .orElseThrow(() -> new BookingStateException("Unknown state: " + stateParam));
        return bookingClient.findAllBookingsByUser(userId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllBookingsByOwner(@RequestHeader(USER_ID_HEADER) Long userId,
                                                           @RequestParam(value = "state", defaultValue = "ALL") String stateParam,
                                                           @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                                           @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        BookingState bookingState = BookingState.from(stateParam)
                .orElseThrow(() -> new BookingStateException("Unknown state: " + stateParam));
        return bookingClient.findAllBookingsByOwner(userId, bookingState, from, size);
    }
}
