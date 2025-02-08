package ru.practicum.service;

import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto saveCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(long catId);

    CategoryDto patchCategory(long catId, NewCategoryDto newCategoryDto);

    List<CategoryDto> getAllCategoriesWithPageable(int from, int size);

    CategoryDto getCategoryById(long catId);
}
