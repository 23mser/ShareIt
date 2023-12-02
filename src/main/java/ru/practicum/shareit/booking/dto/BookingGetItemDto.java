package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingGetItemDto {
    private long id;
    private long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;

    public BookingGetItemDto(long id, long bookerId) {
        this.id = id;
        this.bookerId = bookerId;
    }
}