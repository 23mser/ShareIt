package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingGetItemDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private BookingGetItemDto lastBooking;
    private BookingGetItemDto nextBooking;
    private List<CommentDto> comments;
    private Long requestId;
}
