package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@example.com");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Need a screwdriver");
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
    }

    @Test
    void addNewRequest_shouldReturnItemRequestDto_whenValidUserId() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(ArgumentMatchers.<ItemRequest>any()))
                .thenAnswer(invocation -> {
                    ItemRequest request = invocation.getArgument(0);
                    request.setId(1L);
                    return request;
                });

        ItemRequestDto result = itemRequestService.addNewRequest(1L, itemRequest);

        assertNotNull(result);
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(user.getId(), result.getRequesterId());
    }

    @Test
    void findAllRequestsByUserId_shouldReturnListOfDtos() {
        when(itemRequestRepository.findAllByRequesterId(1L)).thenReturn(List.of(itemRequest));

        Collection<ItemRequestDto> result = itemRequestService.findAllRequestsByUserId(1L);

        assertEquals(1, result.size());
        assertEquals(itemRequest.getDescription(), result.iterator().next().getDescription());
    }

    @Test
    void findAllRequestsByOtherUsers_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ItemRequest> page = new PageImpl<>(List.of(itemRequest));

        when(itemRequestRepository.findOtherUsersRequests(1L, pageable)).thenReturn(page);

        Page<ItemRequest> result = itemRequestService.findAllRequestsByOtherUsers(1L, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(itemRequest.getDescription(), result.getContent().getFirst().getDescription());
    }

    @Test
    void findRequestById_shouldReturnDtoWithItems() {
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(1L)).thenReturn(List.of());

        ItemRequestDto result = itemRequestService.findRequestById(1L);

        assertNotNull(result);
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void findRequestById_shouldThrowNotFound_whenNoSuchId() {
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.findRequestById(1L));
    }

    @Test
    void addNewRequest_shouldThrowNotFound_whenUserNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.addNewRequest(1L, itemRequest));
    }
}
