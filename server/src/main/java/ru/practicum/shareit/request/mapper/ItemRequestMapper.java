package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setRequesterId(itemRequest.getRequester().getId());
        dto.setCreated(itemRequest.getCreated());
        return dto;
    }

    public static List<ItemRequestDto> mapToItemRequestDtoList(Collection<ItemRequest> requests) {
        return requests.stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .toList();
    }
}
