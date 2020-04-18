package com.training.controllers;

import com.training.dto.PriceView;
import com.training.models.Price;
import com.training.models.Product;
import com.training.models.enums.Currency;
import com.training.services.DataManagementService;
import com.training.services.PriceService;
import com.training.services.ProductService;
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

import static com.training.controllers.PriceController.PRICES_ATTRIBUTE;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Price Controller integration testing")
@SpringBootTest
@TestPropertySource("/application-test.properties")
public class PriceControllerIntegrationTest {

    @Autowired
    private PriceController priceController;
    @Autowired
    private PriceService priceService;
    @Autowired
    private ProductService productService;
    @Autowired
    private DataManagementService dataManagementService;

    private Price preparedPrice;
    private Product preparedProduct;

    @BeforeEach
    public void generateData() {
        generateProduct();
        generatePrice();
    }

    private void generateProduct() {
        preparedProduct = new Product();
        preparedProduct.setName("productName");
        preparedProduct = productService.saveOrUpdate(preparedProduct);

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
        preparedProduct = null;
        preparedPrice = null;
        dataManagementService.clearData();
    }

    @Test
    void givenNewPrice_whenSave_thenReturnSavedPrice() {
        assertNotNull(preparedProduct);
        assertNotNull(preparedProduct.getId());

        //given
        PriceView newPrice = new PriceView();
        newPrice.setValue(12213.00);
        newPrice.setCurrency(Currency.DOLLAR);
        newPrice.setProductId(preparedProduct.getId());

        //when
        ModelAndView modelAndView = priceController.savePrice(newPrice);

        //then (prepared)
        Map<String, Object> model = modelAndView.getModel();
        Object priceObj = model.get(PriceController.PRICE_ATTRIBUTE);
        assertNotNull(priceObj);
        assertTrue(priceObj instanceof Price);

        //then (target)
        Price resultPrice = (Price) priceObj;
        assertNotNull(resultPrice.getId());
        assertEquals(resultPrice.getValue(), resultPrice.getValue());
        assertEquals(resultPrice.getCurrency(), resultPrice.getCurrency());
    }

    @Test
    void givenNewPriceWithoutProduct_whenSave_thenReturnNull() {
        //given
        PriceView newPrice = new PriceView();
        newPrice.setValue(12213.00);
        newPrice.setCurrency(Currency.DOLLAR);

        //when
        ModelAndView modelAndView = priceController.savePrice(newPrice);

        //then (target)
        Map<String, Object> model = modelAndView.getModel();
        Object priceObj = model.get(PriceController.PRICE_ATTRIBUTE);
        assertNull(priceObj);
    }

    @Test
    void givenExistentPrice_whenSave_thenReturnNull() {
        assertNotNull(preparedPrice.getId());
        assertNotNull(preparedPrice.getValue());
        assertNotNull(preparedPrice.getCurrency());
        assertNotNull(preparedPrice.getProduct());
        assertNotNull(preparedPrice);

        //given
        PriceView existentPrice = new PriceView();
        existentPrice.setId(preparedPrice.getId());
        existentPrice.setValue(preparedPrice.getValue());
        existentPrice.setCurrency(preparedPrice.getCurrency());
        existentPrice.setProductId(preparedPrice.getProduct().getId());

        //when
        ModelAndView modelAndView = priceController.savePrice(existentPrice);

        //then (prepared)
        Map<String, Object> model = modelAndView.getModel();
        Object priceObj = model.get(PriceController.PRICE_ATTRIBUTE);

        //then (target)
        assertNull(priceObj);
    }

    @Test
    void givenNull_whenSave_thenReturnNull() {
        //given
        //when
        ModelAndView modelAndView = priceController.savePrice(null);

        //then (prepared)
        Map<String, Object> model = modelAndView.getModel();
        Object priceObj = model.get(PriceController.PRICE_ATTRIBUTE);

        //then (target)
        assertNull(priceObj);
    }


    @Test
    void givenCurrency_whenFindByCurrency_thenReturnPriceList() {
        //given
        assertNotNull(preparedPrice);
        assertNotNull(preparedPrice.getCurrency());

        //when
        ModelAndView modelAndView = priceController.findPrice(preparedPrice.getCurrency(),
                null, null, null);

        //then (prepared)
        Map<String, Object> model = modelAndView.getModel();
        Object pricesObj = model.get(PRICES_ATTRIBUTE);
        assertTrue(pricesObj instanceof List);
        List priceList = (List) pricesObj;
        assertTrue(!priceList.isEmpty());
        assertTrue(priceList.get(0) instanceof Price);

        //then (target)
        Price resultPrice = (Price) priceList.get(0);
        assertEquals(preparedPrice.getCurrency(), resultPrice.getCurrency());
    }

    @Test
    void givenProduct_whenFindByProduct_thenReturnPriceList() {
        //given
        assertNotNull(preparedPrice);
        assertNotNull(preparedPrice.getProduct());
        assertNotNull(preparedPrice.getProduct().getId());

        //when
        ModelAndView modelAndView = priceController.findPrice(null,
                preparedPrice.getProduct().getId(), null, null);

        //then (prepared)
        Map<String, Object> model = modelAndView.getModel();
        Object pricesObj = model.get(PRICES_ATTRIBUTE);
        assertTrue(pricesObj instanceof List);
        List priceList = (List) pricesObj;
        assertTrue(!priceList.isEmpty());
        assertTrue(priceList.get(0) instanceof Price);

        //then (target)
        Price resultPrice = (Price) priceList.get(0);
        assertEquals(preparedPrice.getProduct(), resultPrice.getProduct());
    }

    @Test
    void givenPriceRange_whenFindByPriceRange_thenReturnPriceList() {
        //given
        assertNotNull(preparedPrice);
        assertNotNull(preparedPrice.getValue());
        assertNotNull(preparedPrice.getCurrency());

        //when
        double from = preparedPrice.getValue() - 1;
        double to = preparedPrice.getValue() + 1;

        ModelAndView modelAndView = priceController.findPrice(preparedPrice.getCurrency(), null, from, to);

        //then (prepared)
        Map<String, Object> model = modelAndView.getModel();
        Object pricesObj = model.get(PRICES_ATTRIBUTE);
        assertTrue(pricesObj instanceof List);
        List priceList = (List) pricesObj;
        assertTrue(!priceList.isEmpty());
        assertTrue(priceList.get(0) instanceof Price);

        //then (target)
        Price resultPrice = (Price) priceList.get(0);
        assertEquals(preparedPrice.getCurrency(), resultPrice.getCurrency());
        assertTrue(preparedPrice.getValue() >= from);
        assertTrue(preparedPrice.getValue() <= to);
    }

    @Test
    void givenIncorrectPriceRange_whenFindByPriceRange_thenReturnNull() {
        //given
        //when
        ModelAndView modelAndView = priceController.findPrice(preparedPrice.getCurrency(), null, 1.0, -1.0);

        //then prepared
        Map<String, Object> model = modelAndView.getModel();
        Object pricesObj = model.get(PRICES_ATTRIBUTE);

        //then
        assertNull(pricesObj);
    }

    @Test
    void givenNonExistent_whenFind_thenNull() {
        //given
        //when
        ModelAndView modelAndView = priceController.findPrice(null, 43234234234L, 22.00, 3232.00);

        //then prepared
        Map<String, Object> model = modelAndView.getModel();
        Object pricesObj = model.get(PRICES_ATTRIBUTE);
        assertTrue(pricesObj instanceof List);
        List priceList = (List) pricesObj;
        //then
        assertNotNull(priceList);
        assertTrue(priceList.isEmpty());
    }


}
