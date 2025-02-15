package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.dto.UserShortDto;
import ru.practicum.exception.CompilationNotFoundException;
import ru.practicum.model.Category;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final EventRepository eventRepository;

    private final ModelMapper mapper;

    @Override
    @Transactional
    public CompilationDto saveCompilation(NewCompilationDto compilationDto) {
        log.info("saveCompilation started for {}", compilationDto);
        Compilation compilation = mapper.map(compilationDto, Compilation.class);
        List<Long> eventIds = compilationDto.getEvents();
        if (eventIds != null && !eventIds.isEmpty()) {
            List<Event> events = eventRepository.findAllById(eventIds);
            compilation.setEvents(events);
        }
        Compilation savedCompilation = compilationRepository.save(compilation);
        CompilationDto savedCompilationDto = mapper.map(savedCompilation, CompilationDto.class);

        if (eventIds != null && !eventIds.isEmpty()) {
            List<EventShortDto> eventShortDtos = savedCompilation.getEvents().stream()
                    .map(event -> {
                        User initiator = event.getInitiator();
                        Category category = event.getCategory();
                        EventShortDto eventShortDto = mapper.map(event, EventShortDto.class);
                        eventShortDto.setInitiator(mapper.map(initiator, UserShortDto.class));
                        eventShortDto.setCategory(mapper.map(category, CategoryDto.class));
                        return eventShortDto;
                    })
                    .toList();
            savedCompilationDto.setEvents(eventShortDtos);
        }
        log.info("saveCompilation finished for {}", savedCompilationDto);
        return savedCompilationDto;
    }

    @Override
    public void deleteCompilation(long compId) {
        log.info("deleteCompilation started for {}", compId);
        compilationRepository.deleteById(compId);
        log.info("deleteCompilation finished for {}", compId);
    }

    @Override
    @Transactional
    public CompilationDto patchCompilation(long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilationFromRepository = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException(String.format("Compilation %d not found", compId)));

        if (updateCompilationRequest != null) {
            if (updateCompilationRequest.getEvents() != null) {
                List<Event> events = eventRepository.findAllById(updateCompilationRequest.getEvents());
                compilationFromRepository.setEvents(events);
            }
            if (updateCompilationRequest.getPinned() != null) {
                compilationFromRepository.setPinned(updateCompilationRequest.getPinned());
            }
            if (updateCompilationRequest.getTitle() != null) {
                compilationFromRepository.setTitle(updateCompilationRequest.getTitle());
            }
            Compilation savedCompilation = compilationRepository.save(compilationFromRepository);
            CompilationDto result = mapper.map(savedCompilation, CompilationDto.class);
            result.setEvents(savedCompilation.getEvents().stream()
                    .map(event -> mapper.map(event, EventShortDto.class))
                    .toList());
            log.info("patchCompilation finished for {}", result);
        }
        CompilationDto result = mapper.map(compilationFromRepository, CompilationDto.class);
        result.setEvents(compilationFromRepository.getEvents().stream()
                .map(event -> mapper.map(event, EventShortDto.class))
                .toList());
        log.info("patchCompilation finished for {}", result);
        return result;
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        PageRequest page = PageRequest.of(from / size, size);
        Page<Compilation> compilations = compilationRepository.findAllByPinned(pinned, page);

        List<CompilationDto> compilationDtos = compilations.stream()
                .map(compilation -> mapper.map(compilation, CompilationDto.class))
                .toList();

        log.info("getCompilations finished for {}", compilationDtos);
        return compilationDtos;
    }

    @Override
    public CompilationDto getCompilation(long compId) {
        log.info("getCompilation started for {}", compId);
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException(String.format("Compilation %d not found", compId)));
        CompilationDto compilationDto = mapper.map(compilation, CompilationDto.class);
        log.info("getCompilation finished for {}", compilationDto);
        return compilationDto;
    }
}
