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
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.service.CategoryService;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("createCategory for {} started", newCategoryDto);
        CategoryDto categoryDto = categoryService.saveCategory(newCategoryDto);
        log.info("createCategory for {} finished", categoryDto);
        return categoryDto;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable long catId) {
        log.info("deleteCategory for {} started", catId);
        categoryService.deleteCategory(catId);
        log.info("deleteCategory for {} finished", catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto editCategory(@PathVariable long catId, @RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("editCategory for {} {} started", catId, newCategoryDto);
        CategoryDto categoryDto = categoryService.editCategory(catId, newCategoryDto);
        log.info("editCategory for {} {} finished", catId, categoryDto);
        return categoryDto;
    }
}
