package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAllItemsByUserId() {
        Long userId = 1L;
        Item item = new Item();
        item.setId(1L);
        when(itemRepository.findAllByOwnerId(userId)).thenReturn(List.of(item));

        List<ItemDto> result = new ArrayList<>(itemService.findAllByUser(userId));

        assertThat(result).hasSize(1);
        verify(itemRepository).findAllByOwnerId(userId);
    }

    @Test
    void shouldReturnItemById() {
        Long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(Collections.emptyList());

        ItemDto result = itemService.findItem(itemId);

        assertThat(result).isNotNull();
        verify(itemRepository).findById(itemId);
    }

    @Test
    void shouldThrowWhenItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.findItem(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldReturnEmptyListIfSearchTextIsEmpty() {
        assertThat(itemService.searchItems("")).isEmpty();
    }

    @Test
    void shouldReturnItemsMatchingText() {
        when(itemRepository.search("дрель")).thenReturn(List.of(new Item()));

        assertThat(itemService.searchItems("дрель")).hasSize(1);
    }

    @Test
    void shouldCreateItemIfUserExists() {
        Long userId = 1L;
        Item item = new Item();
        item.setName("Item");
        item.setDescription("desc");

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        ItemDto result = itemService.create(item, userId);

        assertThat(result.getName()).isEqualTo("Item");
    }

    @Test
    void shouldThrowIfUserNotFoundOnCreate() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> itemService.create(new Item(), 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldUpdateItemIfOwnerMatches() {
        Item existing = new Item();
        existing.setId(1L);
        existing.setName("old");
        existing.setDescription("desc");
        existing.setAvailable(true);
        existing.setOwnerId(1L);

        Item update = new Item();
        update.setName("new");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(itemRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ItemDto result = itemService.updateItem(1L, 1L, update);

        assertThat(result.getName()).isEqualTo("new");
    }

    @Test
    void shouldThrowIfNotOwnerOnUpdate() {
        Item existing = new Item();
        existing.setId(1L);
        existing.setOwnerId(2L);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> itemService.updateItem(1L, 1L, new Item()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldDeleteItemIfOwnerMatches() {
        Item item = new Item();
        item.setId(1L);
        item.setOwnerId(1L);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        itemService.deleteItem(1L, 1L);

        verify(itemRepository).delete(item);
    }

    @Test
    void shouldThrowIfNotOwnerOnDelete() {
        Item item = new Item();
        item.setId(1L);
        item.setOwnerId(2L);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.deleteItem(1L, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldAddCommentIfBookingExists() {
        Long userId = 1L;
        Long itemId = 2L;

        User user = new User();
        user.setId(userId);
        user.setName("user");
        user.setEmail("user@mail.com");

        Item item = new Item();
        item.setId(itemId);
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwnerId(1L);

        CommentRequest request = new CommentRequest();
        request.setText("Comment");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsPastBookingExcludingRejected(eq(userId), eq(itemId), any())).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> inv.getArgument(0));

        CommentDto result = itemService.addNewComment(itemId, userId, request);

        assertThat(result.getText()).isEqualTo("Comment");
    }

    @Test
    void shouldThrowIfNoPastBookingOnComment() {
        Long userId = 1L;
        Long itemId = 2L;
        CommentRequest request = new CommentRequest();
        request.setText("Comment");

        User user = new User();
        user.setId(userId);

        Item item = new Item();
        item.setId(itemId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsPastBookingExcludingRejected(eq(userId), eq(itemId), any())).thenReturn(false);

        assertThatThrownBy(() -> itemService.addNewComment(itemId, userId, request))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldReturnShortDtoList() {
        when(itemRepository.search("дрель")).thenReturn(List.of(new Item()));

        List<ItemShortDto> result = itemService.searchItemsWithShortDto("дрель");

        assertThat(result).hasSize(1);
    }
}
