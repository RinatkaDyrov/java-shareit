package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.CommentRequest;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    Collection<ItemDto> findAllByUser(Long userId);

    ItemDto findItem(Long itemId);

    Collection<ItemDto> searchItems(String text);

    ItemDto create(Item item, Long userId);

    ItemDto updateItem(Long userId, Long itemId, Item item);

    void deleteItem(Long itemId, Long userId);

    CommentDto addNewComment(Long itemId, Long userId, CommentRequest request);

    List<ItemShortDto> searchItemsWithShortDto(String description);
}
