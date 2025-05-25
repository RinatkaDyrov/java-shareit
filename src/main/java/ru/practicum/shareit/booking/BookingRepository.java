package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Collection<Booking> findAllByItemOwnerIdOrderByStatusAscStartAsc(Long ownerId);

    Collection<Booking> findByBookerIdOrderByStartDesc(Long userId);

    Collection<Booking> findByBookerIdAndStartBeforeAndEndAfter(
            Long userId,
            LocalDateTime now,
            LocalDateTime now1);

    List<Booking> findByBookerIdAndEndBefore(Long userId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartAfter(Long userId, LocalDateTime now);

    @Query("""
            SELECT b FROM Booking b
            JOIN Item i ON b.item.id = i.id
            WHERE b.booker.id = :userId
            AND i.id = :itemId AND b.status = 'APPROVED'
            AND b.end < :now
            """)
    List<Booking> getAllUserBookings(Long userId, Long itemId, LocalDateTime now);
}