package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createBookingShouldThrowIfEndBeforeStart() {
        BookingRequest request = new BookingRequest();
        request.setStart(LocalDateTime.now().plusDays(2));
        request.setEnd(LocalDateTime.now().plusDays(1));
        request.setItemId(1L);

        assertThatThrownBy(() -> bookingService.createBooking(1L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("даты бронирования");
    }

    @Test
    void createBookingShouldThrowIfItemNotFound() {
        BookingRequest request = new BookingRequest();
        request.setStart(LocalDateTime.now());
        request.setEnd(LocalDateTime.now().plusDays(1));
        request.setItemId(999L);

        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(1L, request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void approvedBookingShouldThrowIfNotOwner() {
        Booking booking = new Booking();
        Item item = new Item();
        item.setOwnerId(2L);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.approvedBooking(1L, 1L, "true"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("владелец");
    }

    @Test
    void findBookingByUserAndStateShouldThrowIfStateInvalid() {
        assertThatThrownBy(() -> bookingService.findBookingByUserAndState(1L, "BAD"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void findBookingByOwnerShouldThrowIfUserNotExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.findBookingByOwner(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findBookingByUserAndStateShouldReturnBookings() {
        when(bookingRepository.findByBookerIdOrderByStartDesc(1L)).thenReturn(List.of());
        List<BookingDto> result = (List<BookingDto>) bookingService.findBookingByUserAndState(1L, "ALL");
        assertThat(result).isNotNull();
    }
}
