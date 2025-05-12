package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;

/**
 * TODO Sprint add-controllers.
 * return new ItemDto(
 *                 item.getName(),
 *                 item.getDescription(),
 *                 item.isAvailable(),
 *                 item.getRequest() != null ? item.getRequest().getId() : null
 *         );
 */
@Data
@AllArgsConstructor
public class ItemDto {
    private String name;
    private String description;
    private boolean available;
    private Long request;
}