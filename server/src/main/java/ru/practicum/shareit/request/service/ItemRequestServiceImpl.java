package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto addNewRequest(Long userId, ItemRequest itemRequest) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с данным ID не найден"));
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequest newRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.mapToItemRequestDto(newRequest);
    }

    @Override
    public Collection<ItemRequestDto> findAllRequestsByUserId(Long userId) {
        Collection<ItemRequest> requests = itemRequestRepository.findAllByRequesterId(userId);
        return ItemRequestMapper.mapToItemRequestDtoList(requests);
    }

    @Override
    public Page<ItemRequest> findAllRequestsByOtherUsers(Long userId, Pageable pageable) {
        return itemRequestRepository.findOtherUsersRequests(userId, pageable);
    }

    @Override
    public ItemRequestDto findRequestById(Long requestId) {
        ItemRequestDto dto = itemRequestRepository.findById(requestId)
                .map(ItemRequestMapper::mapToItemRequestDto)
                .orElseThrow(() -> new NotFoundException("Вещь с таким айди не найдена"));
        List<ItemShortDto> items = itemRepository.findAllByRequestId(requestId)
                .stream()
                .map(ItemMapper::mapToShortItemDto)
                .toList();
        dto.setItems(items);
        return dto;
    }
}
