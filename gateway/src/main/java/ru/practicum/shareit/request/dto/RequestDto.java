package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestDto {
    @NotBlank(message = "Необходимо указать описание запроса")
    @Size(max = 2000, message = "Максимальная длина описания - 2000 символов")
    String description;
}
