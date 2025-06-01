package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    Collection<ItemRequest> findAllByRequesterId(Long userId);

    @Query("""
            SELECT i FROM ItemRequest i
            WHERE i.requester.id <> :userId
            """)
    Page<ItemRequest> findOtherUsersRequests(@Param("userId") Long userId, Pageable pageable);

    boolean existsByIdAndRequesterId(Long requestId, Long userId);
}