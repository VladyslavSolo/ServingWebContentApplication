package com.training.controllers;

import com.training.dto.PriceView;
import com.training.models.Price;
import com.training.models.enums.Currency;
import com.training.services.PriceService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/price")
public class PriceController {

    public static final String PRICE_ATTRIBUTE = "price";
    public static final String PRICES_ATTRIBUTE = "prices";

    private final PriceService priceService;

    @GetMapping
    public ModelAndView currencyMainPage() {
        return new ModelAndView("/price/index");
    }

    @GetMapping("/find")
    public ModelAndView priceFindPage() {
        return new ModelAndView("/price/find");
    }

    @GetMapping("/add")
    public ModelAndView priceAddPage() {
        return new ModelAndView("/price/add");
    }

    @PostMapping("/find")
    public ModelAndView findPrice(@RequestParam(value = "currency", required = false) Currency currency,
                                  @RequestParam(value = "id", required = false) Long productId,
                                  @RequestParam(value = "from", required = false) Double from,
                                  @RequestParam(value = "to", required = false) Double to) {

        ModelAndView priceFindPage = priceFindPage();
        List<Price> prices = null;

        if (productId != null) {
            prices = priceService.getPricesByProductId(productId);

        } else if (currency != null
                && from != null && to != null
                && from >= 0 && to >= 0
                && (from <= to)) {
            prices = priceService.getPricesByPriceRange(from, to, currency);

        } else if (currency != null && from == null && to == null) {
            prices = priceService.getPricesByCurrency(currency);
        }

        priceFindPage.addObject(PRICES_ATTRIBUTE, prices);
        return priceFindPage;
    }

    @PostMapping("/add")
    public ModelAndView savePrice(@Valid @ModelAttribute PriceView priceView) {
        ModelAndView modelAndView = priceAddPage();

        if (priceView != null && priceView.getId() == null) {
            modelAndView.addObject(PRICE_ATTRIBUTE, priceService.saveOrUpdate(priceView));
        }
        return modelAndView;
    }

//    @PostMapping("/update")
//    public ModelAndView updatePrice(@Valid @ModelAttribute PriceView priceView) {
//        ModelAndView modelAndView = priceUpdatePage();
//
//        if (priceView != null && priceView.getId() != null) {
//            modelAndView.addObject(PRICE_ATTRIBUTE, priceService.saveOrUpdate(priceView));
//        }
//        return modelAndView;
//    }
//
//    @PostMapping("delete")
//    public ModelAndView deletePrice(@RequestParam("id") Long id) {
//        if (id != null) {
//            priceService.deleteById(id);
//        }
//        return priceDeletePage();
//    }

    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }
}
