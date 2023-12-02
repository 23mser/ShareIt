package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestIncomeDto;
import ru.practicum.shareit.request.dto.ItemRequestLongDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.exceptions.RequestNotFoundException;
import ru.practicum.shareit.request.exceptions.RequestValidateException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto createRequest(ItemRequestIncomeDto requestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден."));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestDto, user);
        itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestLongDto> findUserRequests(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден."));

        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorId(userId, sort);
        Map<ItemRequest, List<Item>> itemsByRequests = itemRepository.findAllByRequestId(itemRequests)
                .stream().collect(Collectors.groupingBy(Item::getItemRequest, Collectors.toList()));

        return ItemRequestMapper.toListItemRequestDtoForOwner(itemRequests, itemsByRequests);
    }

    @Override
    public List<ItemRequestLongDto> findAllRequests(int from, int size, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден."));
        if (from < 0 || size <= 0) {
            throw new RequestValidateException("Ошибка пагинации.");
        }

        Pageable pageable = PageRequest.of(from == 0 ? 0 : (from / size), size, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequest> itemRequests = itemRequestRepository.findAllForeign(userId, pageable).toList();
        Map<ItemRequest, List<Item>> itemsByRequests = itemRepository.findAllByRequestId(itemRequests)
                .stream().collect(Collectors.groupingBy(Item::getItemRequest, Collectors.toList()));

        return ItemRequestMapper.toListItemRequestDtoForOwner(itemRequests, itemsByRequests);
    }

    @Override
    public ItemRequestLongDto findRequestById(Long requestId, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден."));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException("Запрос не найден."));
        List<Item> itemsByRequest = itemRepository.findAllByItemRequest(itemRequest);
        return ItemRequestMapper.toItemRequestDtoForOwner(itemRequest, itemsByRequest);
    }
}
