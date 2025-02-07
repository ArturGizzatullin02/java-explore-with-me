//package ru.practicum.controller;
//
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.PatchMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//import ru.practicum.dto.CategoryDto;
//import ru.practicum.dto.NewCategoryDto;
//
//@RestController
//@Slf4j
//@RequiredArgsConstructor
//@Validated
//public class AdminCategoryController {
//
//    private final CategoryService categoryService;
//
//    @PostMapping("/admin/categories")
//    public CategoryDto createCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
//        log.info("createCategory for {} started", newCategoryDto);
//        CategoryDto categoryDto = categoryService.saveCategory(newCategoryDto);
//        log.info("createCategory for {} finished", categoryDto);
//        return categoryDto;
//    }
//
//    @DeleteMapping("/admin/categories/{catId}")
//    public void deleteCategory(@PathVariable long catId) {
//        log.info("deleteCategory for {} started", catId);
//        categoryService.deleteCategory(catId);
//        log.info("deleteCategory for {} finished", catId);
//    }
//
//    @PatchMapping("/admin/categories/{catId}")
//    public CategoryDto patchCategory(@PathVariable long catId, @RequestBody @Valid NewCategoryDto newCategoryDto) {
//        log.info("patchCategory for {} {} started", catId, newCategoryDto);
//        CategoryDto categoryDto = categoryService.patchCategory(catId, newCategoryDto);
//        log.info("patchCategory for {} {} finished", catId, categoryDto);
//        return categoryDto;
//    }
//}
