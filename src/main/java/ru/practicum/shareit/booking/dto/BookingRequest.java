package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingRequest {
    private Long itemId;
    @Future
    private LocalDateTime start;
    @Future
    private LocalDateTime end;
}
