package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public Collection<ItemDto> findItemsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findAllByUser(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@PathVariable Long itemId) {
        return itemService.findItem(itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItem(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @PostMapping
    public ItemDto createItem(@RequestBody @Valid Item item,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.create(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId,
                              @RequestBody Item item) {
        return itemService.updateItem(userId, itemId, item);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) {
        itemService.deleteItem(itemId, userId);
    }
}
