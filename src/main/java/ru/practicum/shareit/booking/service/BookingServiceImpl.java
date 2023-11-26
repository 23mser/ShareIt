package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingGetDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.exceptions.BookingCreateException;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.booking.exceptions.BookingStateException;
import ru.practicum.shareit.booking.exceptions.BookingStatusUpdateException;
import ru.practicum.shareit.booking.exceptions.UserBookOwnItemException;
import ru.practicum.shareit.booking.exceptions.WrongStateException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден."));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
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
        userRepository.findById(userId);
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new BookingNotFoundException("Бронирование не найдено.");
        }
        if (!booking.get().getItem().getOwner().getId().equals(userId)) {
            throw new UserBookOwnItemException("Пользователь является владельцем вещи.");
        }
        if (booking.get().getStatus().name().equals(BookingStatus.APPROVED.name())) {
            throw new BookingStatusUpdateException("Ошибка статуса броинрования.");
        }
        if (approved) {
            booking.get().setStatus(BookingStatus.APPROVED);
        } else {
            booking.get().setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingGetDto(bookingRepository.save(booking.get()));
    }

    @Override
    @Transactional
    public BookingGetDto getBooking(Long bookingId, Long userId) {
        userRepository.findById(userId).orElseThrow();
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new BookingNotFoundException("Бронирование не найдено.");
        }
        if (!booking.get().getBooker().getId().equals(userId) && !booking.get().getItem().getOwner().getId().equals(userId)) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
        return BookingMapper.toBookingGetDto(booking.get());
    }

    @Override
    @Transactional
    public List<BookingGetDto> getAllBookingsByUser(String state, Long userId) {
        userRepository.findById(userId).orElseThrow();
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(BookingState.class, state);
        } catch (Exception e) {
            throw new BookingStateException(state);
        }
        List<Booking> bookingList = new ArrayList<>();

        switch (bookingState) {
            case ALL:
                bookingList = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
            case PAST:
                bookingList = bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookingList = bookingRepository.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now());
                break;
            case CURRENT:
                bookingList = bookingRepository.findByBookerIdCurrent(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookingList = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED);
                break;
        }
        return bookingList.stream().map(BookingMapper::toBookingGetDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<BookingGetDto> getAllBookingsByOwner(String state, Long userId) {
        userRepository.findById(userId).orElseThrow();
        BookingState bookingState;

        try {
            bookingState = BookingState.valueOf(BookingState.class, state);
        } catch (IllegalArgumentException e) {
            throw new WrongStateException(state);
        }
        if (itemRepository.findAllByOwnerId(userId).isEmpty()) {
            throw new ItemNotFoundException("Вещей не найдено.");
        }
        List<Booking> bookingList = new ArrayList<>();

        switch (bookingState) {
            case ALL:
                bookingList = bookingRepository.getAllBookingsByOwner(userId);
                break;
            case PAST:
                bookingList = bookingRepository.findBookingByItemOwnerAndEndIsBefore(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookingList = bookingRepository.findBookingByItemOwnerAndStartIsAfter(userId, LocalDateTime.now());
                break;
            case CURRENT:
                bookingList = bookingRepository.findBookingsByItemOwnerCurrent(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookingList = bookingRepository.findBookingByItemOwnerAndStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository.findBookingByItemOwnerAndStatus(userId, BookingStatus.REJECTED);
                break;
        }
        return bookingList.stream().map(BookingMapper::toBookingGetDto).collect(Collectors.toList());
    }
}
