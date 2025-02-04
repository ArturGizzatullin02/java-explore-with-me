package ru.practicum.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {

    @Email(message = "Некорректный email")
    @NotBlank(message = "Email не может быть пустым")
    @Size(min = 6, max = 254, message = "Размер email должен быть от 6 до 254 символов")
    private String email;

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 2, max = 250, message = "Размер имени пользователя должен быть от 2 до 250 символов")
    private String name;
}
