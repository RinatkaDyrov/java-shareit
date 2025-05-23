package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.request.ItemRequest;

import java.util.Collection;

public interface BookingService {
    BookingDto createBooking(Long userId, BookingRequest request);

    BookingDto approvedBooking(Long userId, Long BookingId, String approve);

    BookingDto findBookingById(Long bookingId, Long userId);

    Collection<BookingDto> findBookingByUserAndState(Long userId, String state);

    Collection<BookingDto> findBookingByOwner(Long ownerId);
}
