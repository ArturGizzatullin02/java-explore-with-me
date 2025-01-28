package ru.practicum;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class GetHitsRequestParametersDto {

    @NotBlank
    private LocalDateTime start;

    @NotBlank
    private LocalDateTime end;

    private Collection<String> uris;

    private boolean unique;
}
