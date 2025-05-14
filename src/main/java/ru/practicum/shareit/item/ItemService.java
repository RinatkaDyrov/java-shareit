package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.Validator;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final Validator validator;

    public Collection<ItemDto> findAllByUser(Long userId) {
        return itemRepository.findAllItems()
                .stream()
                .filter(item -> item.getOwner().equals(userId))
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    public ItemDto findItem(Long itemId) {
        return ItemMapper.mapToItemDto(itemRepository.findItemById(itemId));
    }

    public Collection<ItemDto> searchItems(String text) {
        Collection<Item> result = itemRepository.findAllItems();
        return text.isEmpty() ? Collections.emptyList() :
                result.stream()
                        .filter(Item::getAvailable)
                        .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(text.toLowerCase()))
                        .map(ItemMapper::mapToItemDto)
                        .toList();
    }

    public ItemDto create(Item item, Long userId) {
        if (!validator.isUserExist(userId)) {
            throw new NotFoundException("Пользователя с таким ID не существует");
        }
        Item newItem = itemRepository.create(item, userId);
        return ItemMapper.mapToItemDto(newItem);
    }

    public ItemDto updateItem(Long userId, Long itemId, Item item) {
        Item updItem = itemRepository.findItemById(itemId);
        if (!updItem.getOwner().equals(userId)) {
            throw new NotFoundException("Вносить изменения в поля может только владелец");
        }
        if (item.getName() != null) {
            updItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updItem.setAvailable(item.getAvailable());
        }
        itemRepository.update(itemId, updItem);
        return ItemMapper.mapToItemDto(updItem);
    }

    public void deleteItem(Long itemId) {
        itemRepository.delete(itemId);
    }
}
