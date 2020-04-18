package com.training.controllers;

import com.training.dto.CategoryView;
import com.training.models.Category;
import com.training.services.CategoryService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Collections;

@RestController
@RequestMapping("/category")
public class CategoryController {

    public static final String CATEGORIES_ATTRIBUTE = "categories";
    public static final String CATEGORY_ATTRIBUTE = "category";

    public final CategoryService categoryService;

    @GetMapping
    public ModelAndView categoryMainPage() {
        return new ModelAndView("/category/index");
    }

    @GetMapping("/find")
    public ModelAndView categoryFindPage() {
        return new ModelAndView("/category/find");
    }

    @GetMapping("/add")
    public ModelAndView categoryAddPage() {
        return new ModelAndView("/category/add");
    }

    @GetMapping("/update")
    public ModelAndView categoryUpdatePage() {
        return new ModelAndView("/category/edit");
    }

    @GetMapping("/delete")
    public ModelAndView categoryDeletePage() {
        return new ModelAndView("/category/delete");
    }

    @PostMapping("/find")
    public ModelAndView findCategory(@RequestParam(value = "id", required = false) Long id,
                                     @RequestParam(value = "name", required = false) String name) {

        ModelAndView modelAndView = categoryFindPage();
        if (id != null) {
            Category categoryById = categoryService.getCategoryById(id);
            if (categoryById != null) {
                modelAndView.addObject(CATEGORIES_ATTRIBUTE, Collections.singletonList(categoryById));
            }
        } else if (name != null) {
            modelAndView.addObject(CATEGORIES_ATTRIBUTE, categoryService.getCategoriesByName(name));
        }

        return modelAndView;
    }

    @PostMapping("/add")
    public ModelAndView saveCategory(@Valid @ModelAttribute CategoryView categoryView) {
        ModelAndView modelAndView = categoryAddPage();

        if (categoryView!= null && categoryView.getId() == null) {
            Category category = categoryService.transformDtoToModel(categoryView);
            modelAndView.addObject(CATEGORY_ATTRIBUTE, categoryService.saveOrUpdate(category));
        }
        return modelAndView;
    }

    @PostMapping("/update")
    public ModelAndView updateCategory(@Valid @ModelAttribute CategoryView categoryView) {
        ModelAndView modelAndView = categoryUpdatePage();

        if (categoryView!= null && categoryView.getId() != null) {
            Category category = categoryService.transformDtoToModel(categoryView);
            modelAndView.addObject(CATEGORY_ATTRIBUTE, categoryService.saveOrUpdate(category));
        }
        return modelAndView;
    }

    @PostMapping("/delete")
    public ModelAndView delete(@RequestParam("id") Long id) {
        if (id != null) {
            categoryService.deleteById(id);
        }
        return categoryDeletePage();
    }

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

}
