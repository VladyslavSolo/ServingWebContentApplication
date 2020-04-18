package com.training.controllers;

import com.training.services.DataManagementService;
import com.training.services.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class RootController {

    private final DataManagementService dataManagementService;
    private final ProductService productService;

    @GetMapping
    public ModelAndView mainPage(@RequestParam(value = "page", required = false, defaultValue = "0") Integer page) {
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("products", productService.getAllProducts(page));

        return modelAndView;
    }

    @GetMapping("/generate")
    public ModelAndView generateData() {
        dataManagementService.generateData();
        return new ModelAndView("redirect:/");
    }

    public RootController(DataManagementService dataManagementService, ProductService productService) {
        this.dataManagementService = dataManagementService;
        this.productService = productService;
    }
}
