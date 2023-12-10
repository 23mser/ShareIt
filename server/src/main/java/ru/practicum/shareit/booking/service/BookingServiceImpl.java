package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingGetDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.exceptions.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.exceptions.RequestValidateException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingGetDto createBooking(BookingDto bookingDto, Long userId) {
        User user = checkUser(userId);
        Item item = checkItem(bookingDto.getItemId());
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new BookingCreateException("Ошибка создания бронирования.");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new UserBookOwnItemException("Пользователь является владельцем вещи.");
        }
        if (!item.getAvailable() || bookingDto.getStart().isAfter(bookingDto.getEnd())
                || bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new BookingCreateException("Ошибка создания бронирования.");
        }
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        return BookingMapper.toBookingGetDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingGetDto updateBookingStatus(Long bookingId, Long userId, boolean approved) {
        Booking booking = checkBooking(bookingId);
        Item item = checkItem(booking.getItem().getId());
        boolean trueOwner = item.getOwner().getId().equals(userId);
        if (!trueOwner) {
            throw new UserBookOwnItemException("Пользователь является владельцем вещи.");
        }
        BookingStatus status = Boolean.TRUE.equals(approved) ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        boolean trueStatus = booking.getStatus().equals(status);

        if (trueStatus) {
            throw new BookingStatusUpdateException("Ошибка изменения статуса бронирования.");
        }
        booking.setStatus(status);
        return BookingMapper.toBookingGetDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingGetDto findBookingById(long bookingId, long userId) {
        checkUser(userId);
        Booking booking = checkBooking(bookingId);
        Long itemOwner = booking.getItem().getOwner().getId();
        Long bookingOwner = booking.getBooker().getId();
        boolean itemOrBookingOwner = userId == bookingOwner || userId == itemOwner;

        if (!itemOrBookingOwner) {
            throw new BookingNotFoundException("Ошибка поиска бронирования.");
        }
        return BookingMapper.toBookingGetDto(booking);
    }

    @Override
    @Transactional
    public List<BookingGetDto> findAllBookingsByUser(int from, int size, Long userId, String state) {
        checkUser(userId);
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(BookingState.class, state);
        } catch (Exception e) {
            throw new BookingStateException(state);
        }
        return filterByBookingStateForUser(bookingState, userId, pagination(from, size))
                .stream().map(BookingMapper::toBookingGetDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<BookingGetDto> findAllBookingsByOwner(int from, int size, Long userId, String state) {
        checkUser(userId);
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(BookingState.class, state);
        } catch (IllegalArgumentException e) {
            throw new BookingStateException(state);
        }
        if (itemRepository.findAllByOwnerId(userId).isEmpty()) {
            throw new ItemNotFoundException("Вещей не найдено.");
        }
        return filterByBookingStateForOwner(bookingState, userId, pagination(from, size))
                .stream().map(BookingMapper::toBookingGetDto).collect(Collectors.toList());
    }

    private List<Booking> filterByBookingStateForOwner(BookingState bookingState, Long userId, Pageable pageable) {
        List<Booking> bookingList = new ArrayList<>();
        switch (bookingState) {
            case ALL:
                bookingList = bookingRepository.findAllBookingsByOwner(userId, pageable);
                break;
            case PAST:
                bookingList = bookingRepository.findBookingByItemOwnerAndEndIsBefore(userId, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookingList = bookingRepository.findBookingByItemOwnerAndStartIsAfter(userId, LocalDateTime.now(), pageable);
                break;
            case CURRENT:
                bookingList = bookingRepository.findBookingsByItemOwnerCurrent(userId, LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookingList = bookingRepository.findBookingByItemOwnerAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookingList = bookingRepository.findBookingByItemOwnerAndStatus(userId, BookingStatus.REJECTED, pageable);
                break;
        }
        return bookingList;
    }

    private List<Booking> filterByBookingStateForUser(BookingState bookingState, Long userId, Pageable pageable) {
        List<Booking> bookingList = new ArrayList<>();
        switch (bookingState) {
            case ALL:
                bookingList = bookingRepository.findByBookerIdOrderByStartDesc(userId, pageable);
                break;
            case PAST:
                bookingList = bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookingList = bookingRepository.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), pageable);
                break;
            case CURRENT:
                bookingList = bookingRepository.findByBookerIdCurrent(userId, LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookingList = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookingList = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
                break;
        }
        return bookingList;
    }

    private Pageable pagination(int from, int size) {
        if (from < 0 || size <= 0) {
            throw new RequestValidateException("Ошибка пагинации.");
        }
        return PageRequest.of(from == 0 ? 0 : (from / size), size, Sort.by(Sort.Direction.DESC, "start"));
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден."));
    }

    private Item checkItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Вещь не найдена."));
    }

    private Booking checkBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("Броинрование не найдено."));
    }
}
