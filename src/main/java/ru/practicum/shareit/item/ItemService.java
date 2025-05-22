package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.Validator;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final Validator validator;

    public Collection<ItemDto> findAllByUser(Long userId) {
        return itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    public ItemDto findItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с заданным ID не найдена"));
        return ItemMapper.mapToItemDto(item);
    }

    public Collection<ItemDto> searchItems(String text) {
        return itemRepository.search(text);
    }

    @Transactional
    public ItemDto create(Item item, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким ID не существует"));
        item.setOwner(user);
        Item newItem = itemRepository.save(item);

        return ItemMapper.mapToItemDto(newItem);
    }

    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, Item item) {
        Item updItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с заданным ID не найдена"));
        if (!updItem.getOwner().getId().equals(userId)) {
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
        itemRepository.save(updItem);

        return ItemMapper.mapToItemDto(updItem);
    }

    @Transactional
    public void deleteItem(Long itemId, Long userId) {
        Item itemForDeleting = itemRepository.findById(itemId)
                        .orElseThrow(() -> new NotFoundException("Вещь с заданным ID не найдена"));
        if (!itemForDeleting.getOwner().getId().equals(userId)){
            throw new NotFoundException("Удаление доступно только владельцу");
        }
        itemRepository.delete(itemForDeleting);
    }
}
