package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestService;

import java.util.Collection;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestBody Booking booking,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.createBooking(userId, booking);
    }

    @PatchMapping
    public void approveBooking(@RequestParam boolean approve,
                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        bookingService.approveBooking(userId, approve);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBookingById(@PathVariable Long bookingId,
                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.findBookingById(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDto> findBookingsByUserAndState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                 @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findBookingByUserAndState(userId, state);
    }
}
