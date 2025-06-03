package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    @NotBlank(message = "Необходимо указать название")
    private String name;

    @NotBlank(message = "Необходимо указать описание")
    private String description;

    @NotNull(message = "Необходимо указать статус аренды")
    private Boolean available;

    private Long requestId;
}
