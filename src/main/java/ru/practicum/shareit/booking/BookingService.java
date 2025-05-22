package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.request.ItemRequest;

import java.util.Collection;

public interface BookingService {
    BookingDto createBooking(Long userId, Booking booking);

    void approveBooking(Long userId, boolean approve);

    BookingDto findBookingById(Long bookingId, Long userId);

    Collection<BookingDto> findBookingByUserAndState(Long userId, String state);
}
