package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {

    private List<Integer> events;

    @Builder.Default
    private Boolean pinned = false;

    @Size(min = 1, max = 50, message = "Заголовок подборки должен быть от 1 до 50 символов")
    @NotBlank(message = "Заголовок подборки не может быть пустым")
    private String title;
}
