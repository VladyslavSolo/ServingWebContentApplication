package com.training.services;

import com.training.dto.CategoryView;
import com.training.models.Category;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CategoryService {

    Category getCategoryById(Long id);

    List<Category> getCategoriesByName(String name);

    Category saveOrUpdate(Category category);

    void delete(Category category);

    void deleteById(Long id);

    Category transformDtoToModel(@NotNull CategoryView categoryView);

    List<Category> getAllCategories();

}
