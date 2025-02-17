package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CategoryDto;
import ru.practicum.service.CategoryService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/categories")
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(required = false, defaultValue = "0") int from,
                                           @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("getCategories for from {} size {} started", from, size);
        List<CategoryDto> categoryDtos = categoryService.getAllCategoriesWithPageable(from, size);
        log.info("getCategories for from {} size {} finished, result {}", from, size, categoryDtos);
        return categoryDtos;
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable long catId) {
        log.info("getCategory for category {} started", catId);
        CategoryDto categoryDto = categoryService.getCategoryById(catId);
        log.info("getCategory for {} finished", categoryDto);
        return categoryDto;
    }
}
