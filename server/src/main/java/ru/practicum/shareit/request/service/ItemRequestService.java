package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestLongDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(ItemRequestDto requestDto, Long userId);

    List<ItemRequestLongDto> findUserRequests(Long userId);

    List<ItemRequestLongDto> findAllRequests(int from, int size, Long userId);

    ItemRequestLongDto findRequestById(Long requestId, Long userId);

}
