package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.Validator;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

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
        return result.stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().matches(text) || item.getDescription().matches(text))
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    public ItemDto create(Item item, Long userId) {
        validator.itemValidation(item.getId());
        validator.userValidation(userId);
        return ItemMapper.mapToItemDto(itemRepository.create(item, userId));
    }

    public ItemDto updateItem(Long userId, Long itemId, Item item) {
        Item updItem = itemRepository.findItemById(itemId);
        if (!updItem.getOwner().equals(userId)) {
            throw new RuntimeException("Вносить изменения в поля может только владелец");
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
        validator.removeItem(itemId);
        itemRepository.delete(itemId);
    }
}
