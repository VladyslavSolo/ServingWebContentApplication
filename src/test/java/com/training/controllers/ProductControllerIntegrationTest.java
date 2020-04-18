package com.training.controllers;

import com.training.dto.ProductView;
import com.training.models.Category;
import com.training.models.Price;
import com.training.models.Product;
import com.training.models.enums.Currency;
import com.training.services.CategoryService;
import com.training.services.DataManagementService;
import com.training.services.PriceService;
import com.training.services.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.training.controllers.ProductController.PRODUCTS_ATTRIBUTE;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Product Controller integration testing")
@SpringBootTest
@TestPropertySource("/application-test.properties")
public class ProductControllerIntegrationTest {

    @Autowired
    private ProductController productController;
    @Autowired
    private PriceService priceService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DataManagementService dataManagementService;

    private Category preparedCategory;
    private Product preparedProduct;
    private Price preparedPrice;

    @BeforeEach
    public void generateData() {
        generateCategory();
        generateProduct();
        generatePrice();
    }

    private void generateCategory() {
        preparedCategory = new Category();
        preparedCategory.setName("categoryName");

        preparedCategory = categoryService.saveOrUpdate(preparedCategory);

        assertNotNull(preparedCategory);
        assertNotNull(preparedCategory.getId());
        assertNotNull(preparedCategory.getName());
    }

    private void generateProduct() {
        preparedProduct = new Product();
        preparedProduct.setName("productName");
        preparedProduct.setCategories(new HashSet<Category>() {{
            add(preparedCategory);
        }});
        preparedProduct = productService.saveOrUpdate(preparedProduct);
        preparedCategory = categoryService.saveOrUpdate(preparedCategory);

        assertNotNull(preparedProduct);
        assertNotNull(preparedProduct.getId());
        assertNotNull(preparedProduct.getName());
    }

    private void generatePrice() {
        preparedPrice = new Price(Currency.BYN, 1111.11);
        preparedPrice.setProduct(preparedProduct);
        preparedPrice = priceService.save(preparedPrice);

        assertNotNull(preparedPrice);
        assertNotNull(preparedPrice.getId());
        assertNotNull(preparedPrice.getValue());
        assertNotNull(preparedPrice.getCurrency());
    }

    @AfterEach
    public void clearData() {
        preparedCategory = null;
        preparedProduct = null;
        preparedPrice = null;
        dataManagementService.clearData();
    }

    @Test
    void givenNonExistentProduct_whenSave_thenReturnSavedEntity() {
        assertNotNull(preparedCategory);

        //given
        Product newProduct = new Product();
        Price newPrice = new Price(Currency.EURO, 12.00);

        newProduct.setName("someCategoryName");
        newProduct.addPrice(newPrice);
        newProduct.addCategory(preparedCategory);

        //when
        ResponseEntity<Product> productResponseEntity = productController.saveProduct(newProduct);
        Product resultProduct = productResponseEntity.getBody();

        //then (target)
        assertTrue(productResponseEntity.getStatusCode().is2xxSuccessful());
        assertNotNull(resultProduct);
        assertNotNull(resultProduct.getCategories());
        assertTrue(resultProduct.getCategories().contains(preparedCategory));

        assertFalse(resultProduct.getPrices().isEmpty());
        Price priceFromResultProduct = resultProduct.getPrices().stream().findFirst().get();
        assertNotNull(priceFromResultProduct);
        assertEquals(priceFromResultProduct.getValue(), newPrice.getValue());
        assertEquals(priceFromResultProduct.getCurrency(), newPrice.getCurrency());

    }

    @Test
    void givenNull_whenSave_thenReturnNull() {
        //when
        ResponseEntity<Product> productResponseEntity = productController.saveProduct(null);
        Product resultProduct = productResponseEntity.getBody();

        //then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, productResponseEntity.getStatusCode());
        assertNull(resultProduct);
    }

    @Test
    void givenExistentProduct_whenSave_thenReturnSameUnprocessedProduct() {
        assertNotNull(preparedProduct);
        assertNotNull(preparedProduct.getId());
        //when
        ResponseEntity<Product> productResponseEntity = productController.saveProduct(preparedProduct);
        Product resultProduct = productResponseEntity.getBody();

        //then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, productResponseEntity.getStatusCode());
        assertEquals(resultProduct, preparedProduct);
    }


    @Test
    void givenExistentProduct_whenUpdate_thenReturnUpdatedProduct() {
        assertNotNull(preparedProduct);
        assertNotNull(preparedProduct.getId());
        assertNotNull(preparedProduct.getName());

        //given
        Price newPrice = new Price(Currency.EURO, 12.00);
        Product existentProduct = new Product();
        existentProduct.setId(preparedProduct.getId());
        existentProduct.setName(preparedProduct.getName());
        existentProduct.addPrice(newPrice);
        existentProduct.setName("newProductName");

        ResponseEntity<Product> productResponseEntity = productController.updateProduct(existentProduct);
        Product resultProduct = productResponseEntity.getBody();

        //then
        assertTrue(productResponseEntity.getStatusCode().is2xxSuccessful());
        assertNotNull(resultProduct);
        assertEquals(existentProduct.getName(), resultProduct.getName());
    }

    @Test
    void givenNonExistentProduct_whenUpdate_thenReturnNotUpdatedProduct() {
        //given
        Price newPrice = new Price(Currency.EURO, 12.00);
        Product nonExistentProduct = new Product();
        nonExistentProduct.setId(5435325L);
        nonExistentProduct.setName("someProductName");
        nonExistentProduct.addPrice(newPrice);
        nonExistentProduct.setName("newProductName");

        ResponseEntity<Product> productResponseEntity = productController.updateProduct(nonExistentProduct);
        Product resultProduct = productResponseEntity.getBody();

        //then
        assertNull(resultProduct);
    }

    @Test
    void givenNull_whenUpdate_thenReturnNull() {
        //given
        //when
        ResponseEntity<Product> productResponseEntity = productController.updateProduct(null);
        Product resultProduct = productResponseEntity.getBody();

        //then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, productResponseEntity.getStatusCode());
        assertEquals(null, resultProduct);
    }

    @Test
    void givenProductId_whenFindByCode_thenReturnProductList() {
        //given
        assertNotNull(preparedProduct);
        assertNotNull(preparedProduct.getId());

        //when
        ProductView productView = new ProductView();
        productView.setId(preparedProduct.getId());
        ModelAndView modelAndView = productController.findProducts(productView);

        //then (prepared)
        Map<String, Object> model = modelAndView.getModel();
        Object productObj = model.get(PRODUCTS_ATTRIBUTE);
        assertTrue(productObj instanceof List);
        List productList = (List) productObj;
        assertTrue(!productList.isEmpty());
        assertTrue(productList.get(0) instanceof Product);

        //then (target)
        Product product = (Product) productList.get(0);
        assertNotNull(product);
        assertNotNull(product.getId());
    }

    @Test
    void givenNonExistentProductId_whenFindByCode_thenReturnNull() {
        //given
        ProductView productView = new ProductView();
        productView.setId(23434534543L);

        //when
        ModelAndView modelAndView = productController.findProducts(productView);

        //then
        Map<String, Object> model = modelAndView.getModel();
        Object productObj = model.get(PRODUCTS_ATTRIBUTE);
        assertTrue(productObj instanceof List);
        List productList = (List) productObj;
        assertTrue(productList.isEmpty());
    }

    @Test
    void givenName_whenFindByName_thenReturnProductList() {
        assertNotNull(preparedProduct);
        assertNotNull(preparedProduct.getName());

        //given
        ProductView productView = new ProductView();
        productView.setName(preparedProduct.getName());

        //when
        ModelAndView modelAndView = productController.findProducts(productView);

        //then (prepared)
        Map<String, Object> model = modelAndView.getModel();
        Object productObj = model.get(PRODUCTS_ATTRIBUTE);
        assertTrue(productObj instanceof List);
        List productList = (List) productObj;
        assertTrue(!productList.isEmpty());
        assertTrue(productList.get(0) instanceof Product);

        //then (target)
        Product resultProduct = (Product) productList.get(0);
        assertNotNull(resultProduct);
        assertNotNull(resultProduct.getId());
        assertEquals(resultProduct.getName(), preparedProduct.getName());
    }

    @Test
    void givenPrice_whenFindByPrice_thenReturnProductList() {
        assertNotNull(preparedPrice);

        //given
        Price existentPrice = new Price();
        existentPrice.setCurrency(preparedPrice.getCurrency());
        ProductView productView = new ProductView();
        productView.setPrice(preparedPrice.getValue());
        productView.setCurrency(preparedPrice.getCurrency());

        //when
        ModelAndView modelAndView = productController.findProducts(productView);

        //then (prepared)
        Map<String, Object> model = modelAndView.getModel();
        Object productObj = model.get(PRODUCTS_ATTRIBUTE);
        assertTrue(productObj instanceof List);
        List productList = (List) productObj;
        assertTrue(!productList.isEmpty());
        assertTrue(productList.get(0) instanceof Product);

        //then (target)
        Product resultProduct = (Product) productList.get(0);
        assertNotNull(resultProduct);
        assertNotNull(resultProduct.getId());
        assertNotNull(resultProduct.getPrices());
        assertFalse(resultProduct.getPrices().isEmpty());
        Price priceFromResultProduct = resultProduct.getPrices().stream().findFirst().get();

        assertEquals(preparedPrice.getValue(), priceFromResultProduct.getValue());
        assertEquals(preparedPrice.getCurrency(), priceFromResultProduct.getCurrency());
    }

    @Test
    void givenExistentCategoryId_whenFindCategoryId_thenProductList() {
        assertNotNull(preparedCategory);
        assertNotNull(preparedProduct.getId());

        //given
        ProductView productView = new ProductView();
        productView.setId(preparedProduct.getId());

        //when
        ModelAndView modelAndView = productController.findProducts(productView);

        //then (prepared)
        Map<String, Object> model = modelAndView.getModel();
        Object productObj = model.get(PRODUCTS_ATTRIBUTE);
        assertTrue(productObj instanceof List);
        List productList = (List) productObj;
        assertTrue(!productList.isEmpty());
        assertTrue(productList.get(0) instanceof Product);

        //then (target)
        Product resultProduct = (Product) productList.get(0);
        assertNotNull(resultProduct);
    }

    @Test
    void givenNonExistentCategoryId_whenFindCategoryId_then() {
        //given
        ProductView productView = new ProductView();
        Long nonExistentCategoryId = 56743456L;
        productView.setCategoryId(nonExistentCategoryId);

        //when
        ModelAndView modelAndView = productController.findProducts(productView);

        //then
        Map<String, Object> model = modelAndView.getModel();
        Object productObj = model.get(PRODUCTS_ATTRIBUTE);
        assertNull(productObj);
    }

    @Test
    void givenIncorrectData_whenFind_thenReturnNull() {
        //given
        ProductView productView = new ProductView();
        productView.setId(34534345L);
        productView.setCategoryId(234234234L);
        productView.setPrice(2342342.33);
        productView.setCurrency(Currency.BYN);
        productView.setName("dfgdfg");

        //when
        ModelAndView modelAndView = productController.findProducts(productView);

        //then
        Map<String, Object> model = modelAndView.getModel();
        Object productObj = model.get(PRODUCTS_ATTRIBUTE);
        assertTrue(productObj instanceof List);
        List productList = (List) productObj;
        assertTrue(productList.isEmpty());
    }

}
