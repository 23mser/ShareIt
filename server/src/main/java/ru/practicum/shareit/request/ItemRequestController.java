package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestLongDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto createRequest(@RequestBody ItemRequestDto requestDto,
                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.createRequest(requestDto, userId);
    }

    @GetMapping
    public List<ItemRequestLongDto> findUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.findUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestLongDto> findAllRequests(@RequestParam(defaultValue = "0") int from,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.findAllRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestLongDto findRequestById(@PathVariable("requestId") Long requestId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.findRequestById(requestId, userId);
    }
}
