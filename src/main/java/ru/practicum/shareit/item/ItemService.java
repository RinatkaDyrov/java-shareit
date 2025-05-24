package ru.practicum.shareit.item;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    public Collection<ItemDto> findAllByUser(Long userId) {
        return itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    public ItemDto findItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с заданным ID не найдена"));
        BookingShortDto lastBooking = bookingRepository
                .findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(itemId, LocalDateTime.now(), Status.APPROVED)
                .map(BookingMapper::mapToBookingShortDto)
                .orElse(null);
        BookingShortDto nextBooking = bookingRepository
                .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(itemId, LocalDateTime.now(), Status.APPROVED)
                .map(BookingMapper::mapToBookingShortDto)
                .orElse(null);
        Collection<Comment> comments = commentRepository.findAllByItemId(itemId);

        return ItemMapper.mapToItemDtoWithoutBookings(item,
                CommentMapper.mapCommentToDtoList(comments)
        );
    }

    public Collection<ItemDto> searchItems(String text) {
        return text.isEmpty() ? Collections.emptyList() : ItemMapper.mapToItemDtoList(itemRepository.search(text));
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
        if (!itemForDeleting.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Удаление доступно только владельцу");
        }
        itemRepository.delete(itemForDeleting);
    }

    public CommentDto addNewComment(Long itemId, Long userId, CommentRequest request) {
        if (!bookingRepository.existsByBookerIdAndItemIdAndEndBeforeAndStatus(userId,
                itemId,
                LocalDateTime.now(),
                Status.APPROVED)
        ) {
            throw new ValidationException("Комментарии могут оставлять только пользователи," +
                    " осуществлявшие аренду этой вещи");
        }
        Comment comment = new Comment();
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с данным ID не найден"));
        Item commentedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с данным ID не найдена"));
        comment.setText(request.getText());
        comment.setItem(commentedItem);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        Comment newComment = commentRepository.save(comment);
        return CommentMapper.mapCommentToDto(newComment);
    }
}
