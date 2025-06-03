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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
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

    @Test
    void createBookingShouldReturnBookingWhenValidData() {
        BookingRequest request = new BookingRequest();
        request.setStart(LocalDateTime.now().plusHours(1));
        request.setEnd(LocalDateTime.now().plusHours(2));
        request.setItemId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        item.setOwnerId(2L);

        User user = new User();
        user.setId(1L);

        Booking savedBooking = new Booking();
        savedBooking.setId(123L);
        savedBooking.setItem(item);
        savedBooking.setBooker(user);
        savedBooking.setStart(request.getStart());
        savedBooking.setEnd(request.getEnd());
        savedBooking.setStatus(Status.WAITING);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.save(any())).thenReturn(savedBooking);

        BookingDto dto = bookingService.createBooking(1L, request);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(123L);
    }

    @Test
    void createBookingShouldReturnBookingDto() {
        BookingRequest request = new BookingRequest();
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));
        request.setItemId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setOwnerId(2L);
        item.setAvailable(true);

        User user = new User();
        user.setId(1L);

        Booking savedBooking = new Booking();
        savedBooking.setItem(item);
        savedBooking.setBooker(user);
        savedBooking.setStart(request.getStart());
        savedBooking.setEnd(request.getEnd());
        savedBooking.setStatus(Status.WAITING);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.save(any())).thenReturn(savedBooking);

        BookingDto result = bookingService.createBooking(1L, request);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Status.WAITING);
    }

    @Test
    void approvedBookingShouldApproveWhenWaitingAndTrue() {
        Item item = new Item();
        item.setOwnerId(1L);

        User booker = new User();
        booker.setId(2L);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BookingDto result = bookingService.approvedBooking(1L, 1L, "true");

        assertThat(result.getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    void approvedBookingShouldRejectWhenFalse() {
        Item item = new Item();
        item.setOwnerId(1L);

        User booker = new User();
        booker.setId(2L);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BookingDto result = bookingService.approvedBooking(1L, 1L, "false");

        assertThat(result.getStatus()).isEqualTo(Status.REJECTED);
    }


    @Test
    void approvedBookingShouldCancelWhenUnknownString() {
        Item item = new Item();
        item.setOwnerId(1L);

        User booker = new User();
        booker.setId(2L);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BookingDto result = bookingService.approvedBooking(1L, 1L, "maybe");

        assertThat(result.getStatus()).isEqualTo(Status.CANCELED);
    }

    @Test
    void approvedBookingShouldThrowIfAlreadyApproved() {
        Item item = new Item();
        item.setOwnerId(1L);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStatus(Status.APPROVED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.approvedBooking(1L, 1L, "true"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void findBookingByIdShouldReturnDtoIfUserExists() {
        Item item = new Item();
        item.setOwnerId(1L);

        User booker = new User();
        booker.setId(2L);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.findBookingById(1L, 1L);

        assertThat(result).isNotNull();
    }

    @Test
    void findBookingByIdShouldThrowIfUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> bookingService.findBookingById(1L, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findBookingByOwnerShouldReturnBookings() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByItemOwnerIdOrderByStatusAscStartAsc(1L)).thenReturn(List.of());

        Collection<BookingDto> result = bookingService.findBookingByOwner(1L);

        assertThat(result).isNotNull();
    }


}
