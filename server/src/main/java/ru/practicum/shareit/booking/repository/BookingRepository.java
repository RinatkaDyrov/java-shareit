package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;

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

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.item.id = :itemId " +
            "AND b.end < :now " +
            "AND b.status <> 'REJECTED'")
    boolean existsPastBookingExcludingRejected(@Param("userId") Long userId,
                                               @Param("itemId") Long itemId,
                                               @Param("now") LocalDateTime now);
}
