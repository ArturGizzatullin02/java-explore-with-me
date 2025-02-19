package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CompilationDto;
import ru.practicum.service.CompilationService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationController {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false, defaultValue = "false") Boolean pinned,
                                                @RequestParam(required = false, defaultValue = "0") int from,
                                                @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("getCompilations for {} {} started", pinned, size);
        List<CompilationDto> compilationDtos = compilationService.getCompilations(pinned, from, size);
        log.info("getCompilations for {} {} finished", pinned, compilationDtos);
        return compilationDtos;
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable long compId) {
        log.info("getCompilation for {} started", compId);
        CompilationDto compilationDto = compilationService.getCompilation(compId);
        log.info("getCompilation for {} finished", compilationDto);
        return compilationDto;
    }
}
