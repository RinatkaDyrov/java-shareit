package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public Collection<ItemDto> findAllByUser(Long userId) {
        return itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public ItemDto findItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с заданным ID не найдена"));
        Collection<Comment> comments = commentRepository.findAllByItemId(itemId);
        return ItemMapper.mapToItemDtoWithBookings(item, CommentMapper.mapCommentToDtoList(comments));
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        return text.isEmpty() ? Collections.emptyList() : ItemMapper.mapToItemDtoList(itemRepository.search(text));
    }

    @Transactional
    @Override
    public ItemDto create(Item item, Long userId) {
        if (userRepository.existsById(userId)) {
            item.setOwnerId(userId);
            Item newItem = itemRepository.save(item);
            return ItemMapper.mapToItemDto(newItem);
        } else {
            throw new NotFoundException("Пользователь с данным ID не найден");
        }
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, Long itemId, Item item) {
        Item updItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с заданным ID не найдена"));
        if (!updItem.getOwnerId().equals(userId)) {
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
        if (item.getRequestId() != null) {
            updItem.setRequestId(item.getRequestId());
        }
        itemRepository.save(updItem);

        return ItemMapper.mapToItemDto(updItem);
    }

    @Transactional
    @Override
    public void deleteItem(Long itemId, Long userId) {
        Item itemForDeleting = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с заданным ID не найдена"));
        if (!itemForDeleting.getOwnerId().equals(userId)) {
            throw new NotFoundException("Удаление доступно только владельцу");
        }
        itemRepository.delete(itemForDeleting);
    }

    @Transactional
    @Override
    public CommentDto addNewComment(Long itemId, Long userId, CommentRequest request) {
        log.debug("Запрашиваем пользователя по ID {}", userId);
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с данным ID не найден"));
        log.debug("Запрашиваем вещь по ID {}", userId);
        Item commentedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с данным ID не найдена"));
        Comment comment = CommentMapper.mapToComment(request, author, commentedItem);
        log.debug("Комментарий: {}", comment);
        if (!bookingRepository.existsPastBookingExcludingRejected(userId,
                itemId,
                LocalDateTime.now())) {
            throw new ValidationException("Нужно создать бронирование, только потом комментарий");
        }
        return CommentMapper.mapCommentToDto(commentRepository.save(comment));
    }

    @Override
    public List<ItemShortDto> searchItemsWithShortDto(String text) {
        return ItemMapper.mapToShortItemDtoList(itemRepository.search(text));
    }
}
