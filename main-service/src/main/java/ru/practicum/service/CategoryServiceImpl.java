package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.exception.CategoryAlreadyExistsException;
import ru.practicum.exception.CategoryNotFoundException;
import ru.practicum.exception.DeleteCategoryWithEventsException;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final EventRepository eventRepository;

    private final ModelMapper mapper;

    @Override
    public CategoryDto saveCategory(NewCategoryDto newCategoryDto) {
        log.info("createCategory for {} started", newCategoryDto);
        Category category = mapper.map(newCategoryDto, Category.class);
        if (!categoryRepository.existsByName(newCategoryDto.getName())) {
            Category savedCategory = categoryRepository.save(category);
            CategoryDto result = mapper.map(savedCategory, CategoryDto.class);
            log.info("createCategory for {} finished", result);
            return result;
        } else {
            throw new CategoryAlreadyExistsException(String
                    .format("Category with name %s already exists", category.getName()));
        }
    }

    @Override
    public void deleteCategory(long catId) {
        log.info("deleteCategory for {} started", catId);
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new CategoryNotFoundException(String.format("Category with id %d not found", catId)));
        if (eventRepository.existsByCategory(category)) {
            throw new DeleteCategoryWithEventsException(String
                    .format("Category with id %d has events", catId));
        }
        categoryRepository.deleteById(catId);
        log.info("deleteCategory for {} finished", catId);
    }

    @Override
    public CategoryDto patchCategory(long catId, NewCategoryDto newCategoryDto) {
        log.info("patchCategory for {} {} started", catId, newCategoryDto);
        Category categoryForUpdate = mapper.map(newCategoryDto, Category.class);
        Category categoryFromRepository = categoryRepository.findById(catId)
                .orElseThrow(() -> new CategoryNotFoundException(String.format("Category with id %d not found", catId)));

        categoryForUpdate.setId(categoryFromRepository.getId());

        if (categoryForUpdate.getName() == null) {
            categoryForUpdate.setName(categoryFromRepository.getName());
        } else if (!categoryRepository.existsByName(categoryForUpdate.getName())
                || categoryForUpdate.getName().equals(categoryFromRepository.getName())) {
            categoryRepository.save(categoryForUpdate);
            log.info("patchCategory for {} {} finished", catId, categoryForUpdate);
            return mapper.map(categoryForUpdate, CategoryDto.class);
        } else {
            throw new CategoryAlreadyExistsException(String
                    .format("Category with name %s already exists", categoryFromRepository.getName()));
        }
        categoryRepository.save(categoryForUpdate);
        log.info("patchCategory for {} {} finished", catId, categoryForUpdate);
        return mapper.map(categoryForUpdate, CategoryDto.class);
    }

    @Override
    public List<CategoryDto> getAllCategoriesWithPageable(int from, int size) {
        log.info("getAllCategoriesWithPageable for {} started", from);
        PageRequest page = PageRequest.of(from / size, size);
        Page<Category> categories = categoryRepository.findAll(page);
        List<CategoryDto> categoryDtos = categories.stream()
                .map(category -> mapper.map(category, CategoryDto.class))
                .toList();
        log.info("getAllCategoriesWithPageable for {} finished", categoryDtos);
        return categoryDtos;
    }

    @Override
    public CategoryDto getCategoryById(long catId) {
        log.info("getCategoryById for {} started", catId);
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new CategoryNotFoundException(String.format("Category with id %d not found", catId)));
        CategoryDto categoryDto = mapper.map(category, CategoryDto.class);
        log.info("getCategoryById for {} finished", categoryDto);
        return categoryDto;
    }
}
