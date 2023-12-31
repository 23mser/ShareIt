package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingGetDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping()
    public BookingGetDto createBooking(@RequestBody BookingDto bookingDto,
                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("{bookingId}")
    public BookingGetDto updateBookingStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId,
                                             @RequestParam("approved") boolean approved) {
        return bookingService.updateBookingStatus(bookingId, userId, approved);
    }

    @GetMapping("{bookingId}")
    public BookingGetDto findBookingById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return bookingService.findBookingById(bookingId, userId);
    }

    @GetMapping()
    public List<BookingGetDto> findAllBookingsByUser(@RequestParam(defaultValue = "0") int from,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     @RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return bookingService.findAllBookingsByUser(from, size, userId, state);
    }


    @GetMapping("/owner")
    public List<BookingGetDto> findAllBookingsByOwner(@RequestParam(defaultValue = "0") int from,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return bookingService.findAllBookingsByOwner(from, size, userId, state);
    }
}
