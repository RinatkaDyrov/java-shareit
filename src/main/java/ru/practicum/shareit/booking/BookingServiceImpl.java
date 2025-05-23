package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    public BookingDto createBooking(Long userId, BookingRequest request) {
        if (request.getEnd().isBefore(request.getStart()) || request.getEnd().equals(request.getStart())) {
            throw new RuntimeException("Указаны неверные даты бронирования");
        }
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с данным ID не найдена"));
        if (!item.getAvailable()) {
            throw new RuntimeException("Данная вещь уже забронирована");
        }
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с данным ID не найден"));
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStart(request.getStart());
        booking.setEnd(request.getEnd());
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approvedBooking(Long userId, Long bookingId, String approved) {
        log.debug("Выполняем подтверждение брони");
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование по данному ID не найдено"));
        if (booking.getItem().getOwner().getId().equals(userId)) {
            if (!booking.getStatus().equals(Status.WAITING)) {
                log.debug("Бронирование уже было обработано");
                throw new IllegalStateException("Бронирование уже обработано");
            }
            if (approved.equalsIgnoreCase("true")) {
                log.debug("Подтверждаем бронь");
                booking.setStatus(Status.APPROVED);
            } else if (approved.equalsIgnoreCase("false")) {
                log.debug("Отклоняем бронь");
                booking.setStatus(Status.REJECTED);
            } else {
                booking.setStatus(Status.CANCELED);
            }
            return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
        } else {
            throw new RuntimeException("Подтвердить бронирование может только владелец вещи");
        }
    }

    @Override
    public BookingDto findBookingById(Long bookingId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с данным ID не найден"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование по данному ID не найдено"));
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public Collection<BookingDto> findBookingByUserAndState(Long userId, String state) {
        Collection<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        bookings = switch (state) {
            case "ALL" -> bookingRepository.findByBookerIdOrderByStartDesc(userId);
            case "CURRENT" ->
                    bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
            case "PAST" -> bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case "FUTURE" -> bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            default -> throw new IllegalStateException("Неверный параметр state");
        };

        return BookingMapper.mapToBookingDtoList(bookings);
    }

    @Override
    public Collection<BookingDto> findBookingByOwner(Long ownerId) {
        userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь с данным ID не найден"));
        return BookingMapper.mapToBookingDtoList(bookingRepository.findAllByItemOwnerIdOrderByStatusAscStartAsc(ownerId));
    }
}
