package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.request.ItemRequest;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto createBooking(Long userId, Booking booking) {
        booking.setStatus(Status.WAITING);
        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public void approveBooking(Long userId, boolean approve) {

    }

    @Override
    public BookingDto findBookingById(Long bookingId, Long userId) {
        return null;
    }

    @Override
    public Collection<BookingDto> findBookingByUserAndState(Long userId, String state) {
        return List.of();
    }
}
