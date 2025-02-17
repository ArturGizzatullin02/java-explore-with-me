package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.service.CompilationService;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/admin/compilations")
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("createCompilation for {} started", newCompilationDto);
        CompilationDto compilationDto = compilationService.saveCompilation(newCompilationDto);
        log.info("createCompilation for {} finished", compilationDto);
        return compilationDto;
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("deleteCompilation for {} started", compId);
        compilationService.deleteCompilation(compId);
        log.info("deleteCompilation for {} finished", compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto editCompilation(@PathVariable Long compId,
                                          @RequestBody @Valid UpdateCompilationRequest updateCompilationRequest) {
        log.info("editCompilation for {} started", updateCompilationRequest);
        CompilationDto compilationDto = compilationService.editCompilation(compId, updateCompilationRequest);
        log.info("editCompilation for {} finished", compilationDto);
        return compilationDto;
    }
}
