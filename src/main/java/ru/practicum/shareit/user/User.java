package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class User {
    private Long id;
    private String name;

    @NotNull
    @Email(message = "Неверный формат электронной почты")
    private String email;
}