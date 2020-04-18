package com.training.controllers;

import com.training.dto.CategoryView;
import com.training.models.Category;
import com.training.services.CategoryService;
import com.training.services.DataManagementService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

import static com.training.controllers.CategoryController.CATEGORIES_ATTRIBUTE;
import static com.training.controllers.CategoryController.CATEGORY_ATTRIBUTE;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Category Controller integration testing")
@SpringBootTest
@TestPropertySource("/application-test.properties")
public class CategoryControllerIntegrationTest {

    @Autowired
    private CategoryController categoryController;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DataManagementService dataManagementService;

    private Category preparedCategory;

    @BeforeEach
    public void generateData() {
        preparedCategory = new Category();
        preparedCategory.setName("someName");

        preparedCategory = categoryService.saveOrUpdate(preparedCategory);

        assertNotNull(preparedCategory);
        assertNotNull(preparedCategory.getId());
    }

    @AfterEach
    public void clearData() {
        preparedCategory = null;
        dataManagementService.clearData();
    }

    @Test
    void givenNewCategory_whenSave_thenSavedCategory() {
        //given
        CategoryView newCategory = new CategoryView();
        newCategory.setName("someName");

        //when
        ModelAndView modelAndView = categoryController.saveCategory(newCategory);

        //then (prepared)
        Map<String, Object> model = modelAndView.getModel();
        Object categoryObj = model.get(CATEGORY_ATTRIBUTE);
        assertNotNull(categoryObj);
        assertTrue(categoryObj instanceof Category);

        //then (target)
        Category resultCategory = (Category) categoryObj;
        assertNotNull(resultCategory.getId());
        assertEquals(newCategory.getName(), resultCategory.getName());
    }

    @Test
    void givenNull_whenSave_thenReturnNull() {
        //given
        CategoryView category = null;

        //when
        ModelAndView modelAndView = categoryController.saveCategory(category);

        //then
        Map<String, Object> model = modelAndView.getModel();
        Object result = model.get(CATEGORY_ATTRIBUTE);
        assertNull(result);
    }

    @Test
    void givenExistent_whenSave_thenReturnNull() {
        //given
        CategoryView category = new CategoryView();
        category.setId(1111L);

        //when
        ModelAndView modelAndView = categoryController.saveCategory(category);

        //then
        Map<String, Object> model = modelAndView.getModel();
        Object result = model.get(CATEGORY_ATTRIBUTE);
        assertNull(result);
    }

    @Test
    void givenNull_whenUpdate_thenReturnNull() {
        //given
        CategoryView category = null;

        //when
        ModelAndView modelAndView = categoryController.updateCategory(category);

        //then
        Map<String, Object> model = modelAndView.getModel();
        Object result = model.get(CATEGORY_ATTRIBUTE);
        assertNull(result);
    }

    @Test
    void givenNonExistent_whenUpdate_thenReturnNull() {
        //given
        CategoryView category = new CategoryView();
        category.setId(1111L);

        //when
        ModelAndView modelAndView = categoryController.saveCategory(category);

        //then
        Map<String, Object> model = modelAndView.getModel();
        Object result = model.get(CATEGORY_ATTRIBUTE);
        assertNull(result);
    }

    @Test
    void givenExistentCategory_whenUpdate_thenResultCategory() {
        assertNotNull(preparedCategory);
        assertNotNull(preparedCategory.getId());
        assertNotNull(preparedCategory.getName());
        //given
        CategoryView existentCategory = new CategoryView();
        existentCategory.setName(preparedCategory.getName());
        existentCategory.setId(preparedCategory.getId());

        //when
        ModelAndView modelAndView = categoryController.updateCategory(existentCategory);

        //then (prepared)
        Map<String, Object> model = modelAndView.getModel();
        Object categoryObj = model.get(CATEGORY_ATTRIBUTE);
        assertNotNull(categoryObj);
        assertTrue(categoryObj instanceof Category);

        //then (target)
        Category resultCategory = (Category) categoryObj;
        assertEquals(preparedCategory.getId(), resultCategory.getId());
        assertEquals(preparedCategory.getName(), resultCategory.getName());
    }

    @Test
    public void givenExistentId_whenDelete_thenRemovedEntity() {
        assertNotNull(preparedCategory);
        assertNotNull(preparedCategory.getId());
        //given
        Category categoryInDB = categoryService.getCategoryById(preparedCategory.getId());
        assertNotNull(categoryInDB);

        //when
        categoryController.delete(preparedCategory.getId());

        //then
        categoryInDB = categoryService.getCategoryById(preparedCategory.getId());
        assertNull(categoryInDB);
    }

    @Test
    public void givenName_whenFindByName_thenFoundCategory() {
        //given
        assertNotNull(preparedCategory);
        assertNotNull(preparedCategory.getName());

        //when
        ModelAndView modelAndView = categoryController.findCategory(null, preparedCategory.getName());

        //then (prepared)
        Map<String, Object> model = modelAndView.getModel();
        Object categories = model.get(CATEGORIES_ATTRIBUTE);
        assertTrue(categories instanceof List);
        List categoryList = (List) categories;
        assertTrue(!categoryList.isEmpty());
        assertTrue(categoryList.get(0) instanceof Category);

        //then (target)
        Category resultCategory = (Category) categoryList.get(0);
        assertEquals(preparedCategory.getName(), resultCategory.getName());
    }

    @Test
    public void givenId_whenFindByCode_thenFoundCategory() {
        //given
        assertNotNull(preparedCategory);
        assertNotNull(preparedCategory.getName());

        //when
        ModelAndView modelAndView = categoryController.findCategory(preparedCategory.getId(), null);

        //then (prepared)
        Map<String, Object> model = modelAndView.getModel();
        Object categories = model.get(CATEGORIES_ATTRIBUTE);
        assertTrue(categories instanceof List);
        List categoryList = (List) categories;
        assertTrue(!categoryList.isEmpty());
        assertTrue(categoryList.get(0) instanceof Category);

        //then (target)
        Category resultCategory = (Category) categoryList.get(0);
        assertEquals(preparedCategory.getId(), resultCategory.getId());
    }

    @Test
    void givenNonExistent_whenFind_thenNull() {
        //given
        long nonExistentId = 3453454L; //or
        String nonExistentName = "dfgdfgdfg";

        //when
        ModelAndView modelAndView = categoryController.findCategory(nonExistentId, nonExistentName);

        //then
        Map<String, Object> model = modelAndView.getModel();
        Object categories = model.get(CATEGORIES_ATTRIBUTE);
        assertNull(categories);
    }
}
