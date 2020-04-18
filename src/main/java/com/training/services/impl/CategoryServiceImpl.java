package com.training.services.impl;

import com.training.dto.CategoryView;
import com.training.models.Category;
import com.training.models.Product;
import com.training.repos.CategoryRepository;
import com.training.services.CategoryService;
import com.training.services.ProductService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductService productService;

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.getById(id
        );
    }

    @Override
    public List<Category> getCategoriesByName(String name) {
        return categoryRepository.getCategoriesByName(name);
    }

    @Override
    public Category saveOrUpdate(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public void delete(Category category) {
        categoryRepository.delete(category);
    }

    @Override
    public void deleteById(Long id) {
        Category categoryById = categoryRepository.getById(id);

        for (Product product : categoryById.getProducts()) {
            product.getCategories().remove(categoryById);
        }

        categoryById.getProducts().clear();
        categoryRepository.delete(categoryById);

    }

    @Override
    public Category transformDtoToModel(@NotNull CategoryView categoryView) {
        Category category = new Category();
        Category parent = null;

        Long parentId = categoryView.getParentId();
        if (parentId != null) {
            parent = getCategoryById(parentId);
            if (parent != null) {
                parent.addChild(category);
            }
        }
        category.setParent(parent);
        category.setId(categoryView.getId());
        category.setName(categoryView.getName());

        return category;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public CategoryServiceImpl(CategoryRepository categoryRepository, ProductService productService) {
        this.categoryRepository = categoryRepository;
        this.productService = productService;
    }
}
