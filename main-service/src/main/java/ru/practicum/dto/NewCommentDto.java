package ru.practicum.dto;

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
public class NewCommentDto {

    @Size(min = 1, max = 1000, message = "Текст комментария должен быть от 1 до 1000 символов")
    @NotBlank(message = "Текст комментария не может быть пустым")
    private String text;
}
