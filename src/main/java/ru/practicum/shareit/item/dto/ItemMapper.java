package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@UtilityClass
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        return Item.builder()
                .name(itemDto.getName())
                .id(itemDto.getId())
                .description(itemDto.getDescription())
                .owner(owner)
                .available(itemDto.getAvailable())
                .build();
    }

    public static ItemDto toItemDtoWithBookingAndComments(Item item, Booking lastBooking, Booking nextBooking, List<CommentDto> comments) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(BookingMapper.toBookingGetItemDto(lastBooking))
                .nextBooking(BookingMapper.toBookingGetItemDto(nextBooking))
                .comments(comments)
                .build();
    }

    public static ItemDto toItemDtoWithComments(Item item, List<CommentDto> comments) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(comments)
                .build();
    }
}
