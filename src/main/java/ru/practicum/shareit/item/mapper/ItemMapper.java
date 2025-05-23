package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
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
                List.of() // по умолчанию пустой список, чтобы тест не упал
        );
    }

    public static ItemDto mapToItemDtoWithBookings(Item item,
                                                   BookingShortDto lastBooking,
                                                   BookingShortDto nextBooking,
                                                   List<CommentDto> comments) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking,
                comments != null ? comments : List.of()
        );
    }

    public static ItemDto mapToItemDtoWithoutBookings(Item item, List<CommentDto> comments) {
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

    public static Collection<ItemDto> mapToItemDtoList(Collection<Item> search) {
        return search.stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }
}
