package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto mapToItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                List.of()
        );
    }

    public static ItemDto mapToItemDtoWithBookings(Item item,
                                                   List<CommentDto> comments) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                comments != null ? comments : List.of()
        );
    }

    public static List<ItemDto> mapToItemDtoList(Collection<Item> search) {
        return search.stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    public static ItemShortDto mapToShortItemDto(Item item) {
        return new ItemShortDto(item.getId(), item.getName(), item.getOwnerId());
    }

    public static List<ItemShortDto> mapToShortItemDtoList(Collection<Item> search) {
        return search.stream().map(ItemMapper::mapToShortItemDto).toList();
    }
}
