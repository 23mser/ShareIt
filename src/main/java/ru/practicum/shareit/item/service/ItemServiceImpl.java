package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exceptions.CommentException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.exceptions.OwnerItemException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.exceptions.RequestNotFoundException;
import ru.practicum.shareit.request.exceptions.RequestValidateException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto saveItem(ItemDto itemDto, Long userId) {
        User user = userService.getUserById(userId);
        Item item = ItemMapper.toItem(itemDto, user);
        item.setOwner(user);
        if (itemDto.getRequestId() != null) {
            item.setItemRequest(findItemRequestById(itemDto.getRequestId()));
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto findItemById(Long id, Long userId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Вещь не найдена."));
        List<Comment> comments = commentRepository.findByItemId(id);

        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            return mapItemDtoForOwner(item, now, CommentMapper.toDtoList(comments));
        }
        return ItemMapper.toItemDtoWithBookingAndComments(item, null, null, CommentMapper.toDtoList(comments));

    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Вещь не найдена."));

        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new OwnerItemException("Владелец вещи не найден.");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        List<Comment> comments = commentRepository.findByItemId(id);
        itemRepository.save(item);
        return ItemMapper.toItemDtoWithComments(item, CommentMapper.toDtoList(comments));
    }

    @Override
    public List<ItemDto> findAllItemsOfUser(Long userId, int from, int size) {
        User user = userService.getUserById(userId);
        List<Item> items = itemRepository.findAllByOwnerId(user.getId(), pagination(from, size));
        List<ItemDto> itemsDto = new ArrayList<>();
        mapItemDtoList(itemsDto, items, userId);
        return sortItemsDtoList(itemsDto);
    }

    @Override
    public List<ItemDto> searchItemsByText(String text, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchItemsByText(text, pageRequest)
                .stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        if (commentDto.getText().isBlank()) {
            throw new CommentException("Ошибка комментария.");
        }
        Item item = itemRepository.findById(itemId).orElseThrow();
        User user = userService.getUserById(userId);

        if (!bookingRepository.existsBookingByItemAndBookerAndStatusNotAndStartBefore(item, user, BookingStatus.REJECTED, LocalDateTime.now())) {
            throw new CommentException("Ошибка комментария.");
        }
        Comment comment = CommentMapper.toComment(commentDto, item, user);
        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    private void mapItemDtoList(List<ItemDto> itemsDto, List<Item> items, Long userId) {
        LocalDateTime now = LocalDateTime.now();

        for (Item item : items) {
            List<Comment> comments = commentRepository.findByItemId(item.getId());
            List<CommentDto> commentsDto = CommentMapper.toDtoList(comments);
            if (item.getOwner().getId().equals(userId)) {
                ItemDto itemDto = mapItemDtoForOwner(item, now, commentsDto);
                itemsDto.add(itemDto);
            } else {
                itemsDto.add(ItemMapper.toItemDtoWithComments(item, commentsDto));
            }
        }
    }

    private ItemDto mapItemDtoForOwner(Item item, LocalDateTime now, List<CommentDto> comments) {
        Booking lastBooking = bookingRepository.findBookingByItemIdAndEndBefore(item.getId(), now, BookingStatus.REJECTED)
                .stream().findFirst().orElse(bookingRepository.findBookingsByItemOwnerCurrent(item.getOwner().getId(), now)
                        .stream().findFirst().orElse(null));
        List<Booking> nextBookings = bookingRepository.findBookingByItemIdAndStartAfter(item.getId(), now, BookingStatus.REJECTED);

        return ItemMapper.toItemDtoWithBookingAndComments(item, lastBooking, nextBookings.size() > 0 ? nextBookings.get(nextBookings.size() - 1) : null, comments);
    }

    private ItemRequest findItemRequestById(Long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException("Запрос не найден."));
    }

    private Pageable pagination(int from, int size) {
        if (from < 0 || size <= 0) {
            throw new RequestValidateException("Ошибка пагинации.");
        }
        return PageRequest.of(from == 0 ? 0 : (from / size), size);
    }

    private List<ItemDto> sortItemsDtoList(List<ItemDto> itemsDto) {
        itemsDto.sort((o1, o2) -> {
            if (o1.getNextBooking() == null && o2.getNextBooking() == null) {
                return o1.getId().compareTo(o2.getId());
            }
            if (o1.getNextBooking() != null && o2.getNextBooking() == null) {
                return -1;
            }
            if (o1.getNextBooking() == null) {
                return 1;
            }
            if (o1.getNextBooking().getStart().isBefore(o2.getNextBooking().getStart())) {
                return -1;
            }
            if (o1.getNextBooking().getStart().isAfter(o2.getNextBooking().getStart())) {
                return 1;
            }
            return 0;
        });
        return itemsDto;
    }
}
