package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.common.Validator;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final Validator validator;

    public Collection<Item> findAllItems() {
        return items.values();
    }

    public Item findItemById(Long itemId) {
        return items.get(itemId);
    }


    public Item create(Item item, Long userId) {
        Item newItem = new Item();
        newItem.setId(getNextId());
        newItem.setName(item.getName());
        newItem.setDescription(item.getDescription());
        newItem.setAvailable(item.getAvailable());
        newItem.setOwner(userId);
        newItem.setRequest(item.getRequest());
        items.put(newItem.getId(), newItem);
        validator.trackNewItem(newItem.getId());
        return newItem;
    }

    private Long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public void update(Long itemId, Item updItem) {
        items.put(itemId, updItem);
    }

    public void delete(Long itemId) {
        items.remove(itemId);
        validator.removeItem(itemId);
    }
}
