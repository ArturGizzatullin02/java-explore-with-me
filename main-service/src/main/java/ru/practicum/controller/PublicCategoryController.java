package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CategoryDto;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        log.info("getCategories for {} {} started", from, size);
        List<CategoryDto> categoryDtos = categoryService.getCategories(from, size);
        log.info("getCategories for {} {} finished", from, categoryDtos);
        return categoryDtos;
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategory(@PathVariable long catId) {
        log.info("getCategory for {} started", catId);
        CategoryDto categoryDto = categoryService.getCategory(catId);
        log.info("getCategory for {} finished", categoryDto);
        return categoryDto;
    }
}
