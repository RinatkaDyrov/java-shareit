package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.Collection;

public class BookingMapper {
    public static BookingDto mapToBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    public static Collection<BookingDto> mapToBookingDtoList(Collection<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }

    public static BookingShortDto mapToBookingShortDto(Booking booking) {
        return new BookingShortDto(
                booking.getId(),
                booking.getBooker().getId()
        );
    }
}
