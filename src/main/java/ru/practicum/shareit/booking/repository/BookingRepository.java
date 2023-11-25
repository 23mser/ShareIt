package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking as b where b.item.owner.id = ?1 order by b.start desc")
    List<Booking> getAllBookingsByOwner(Long userId);

    @Query("select b from Booking as b where b.booker.id = ?1 and b.start < ?2 and b.end > ?2 order by b.start desc ")
    List<Booking> findByBookerIdCurrent(Long userId, LocalDateTime now);

    @Query("select b from Booking as b where b.item.owner.id = ?1 and b.start < ?2 and b.end > ?2 order by b.start desc")
    List<Booking> findBookingsByItemOwnerCurrent(Long userId, LocalDateTime now);

    @Query("select b from Booking as b where b.booker.id = ?1 and b.end < ?2 order by b.start desc")
    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime now);

    @Query("select b from Booking as b where b.booker.id = ?1 and b.start > ?2 order by b.start desc")
    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime now);

    @Query("select b from Booking as b where b.booker.id = ?1 and b.status = ?2 order by b.start desc")
    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status);

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    @Query("select b from Booking as b where b.item.owner.id = ?1 and b.status = ?2 order by b.start desc")
    List<Booking> findBookingByItemOwnerAndStatus(Long bookerId, BookingStatus status);

    @Query("select b from Booking as b where b.item.owner.id = ?1 and b.end < ?2 order by b.start desc")
    List<Booking> findBookingByItemOwnerAndEndIsBefore(Long itemOwner, LocalDateTime end);

    @Query("select b from Booking as b where b.item.owner.id = ?1 and b.start > ?2  order by b.start desc")
    List<Booking> findBookingByItemOwnerAndStartIsAfter(Long ownerId, LocalDateTime now);

    @Query("select b from Booking as b where b.item.id = ?1 and b.end < ?2 and b.status<>?3 order by b.start desc")
    List<Booking> findBookingByItemIdAndEndBefore(Long itemId, LocalDateTime now, BookingStatus status);

    @Query("select b from Booking as b where b.item.id = ?1 and b.start > ?2  and b.status<>?3 order by b.start desc")
    List<Booking> findBookingByItemIdAndStartAfter(Long itemId, LocalDateTime now, BookingStatus status);

    boolean existsBookingByItemAndBookerAndStatusNotAndStartBefore(Item item, User booker, BookingStatus status, LocalDateTime time);

}
