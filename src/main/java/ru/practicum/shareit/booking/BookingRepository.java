package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Collection<Booking> findAllByItemOwnerIdOrderByStatusAscStartAsc(Long ownerId);

    Collection<Booking> findByBookerIdOrderByStartDesc(Long userId);

    Collection<Booking> findByBookerIdAndStartBeforeAndEndAfter(
            Long userId,
            LocalDateTime now,
            LocalDateTime now1);

    List<Booking> findByBookerIdAndEndBefore(Long userId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartAfter(Long userId, LocalDateTime now);

    boolean existsByBookerIdAndItemIdAndEndBeforeAndStatus(
            Long bookerId,
            Long itemId,
            LocalDateTime now,
            Status status
    );

    Optional<Booking> findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(
            Long itemId,
            LocalDateTime now,
            Status status);

    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
            Long itemId,
            LocalDateTime now,
            Status status);
}