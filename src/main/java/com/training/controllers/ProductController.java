package com.training.controllers;

import com.training.dto.ProductView;
import com.training.models.Product;
import com.training.services.CategoryService;
import com.training.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import static com.training.controllers.CategoryController.CATEGORIES_ATTRIBUTE;

@RestController
@RequestMapping("/product")
public class ProductController {

    public static final String PRODUCTS_ATTRIBUTE = "products";
    public static final String PRODUCT_ATTRIBUTE = "product";

    private final ProductService productService;
    private final CategoryService categoryService;

    /// Product REST API
    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {

        if (product != null && product.getId() == null) {
            return ResponseEntity.ok(productService.saveOrUpdate(product));
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(product);
    }

    @PutMapping
    public ResponseEntity<Product> updateProduct(@RequestBody Product product) {

        if (product != null && product.getId() != null) {
            return ResponseEntity.ok(productService.saveOrUpdate(product));
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(product);
    }


    @DeleteMapping
    public ResponseEntity<?> deleteProduct(@RequestBody Product product) {
        productService.delete(product);
        return ResponseEntity.ok().build();
    }

    ///

    @PostMapping("/find")
    public ModelAndView findProducts(@ModelAttribute ProductView productView) {
        ModelAndView findPage = findPage();
        if (productView != null) {
            findPage.addObject(PRODUCTS_ATTRIBUTE, productService.find(productView));
        }
        return findPage;
    }

    @GetMapping
    public ModelAndView productPage() {
        return new ModelAndView("/product/index");
    }

    @GetMapping("/find")
    public ModelAndView findPage() {
        return new ModelAndView("/product/find");
    }

    @GetMapping("add")
    public ModelAndView addPage() {
        ModelAndView addPage = new ModelAndView("/product/add");

        addPage.addObject(PRODUCT_ATTRIBUTE, new Product());
        addPage.addObject(CATEGORIES_ATTRIBUTE, categoryService.getAllCategories());
        return addPage;
    }

    @GetMapping("/update")
    public ModelAndView updatePage(@RequestParam(value = "id", required = false) Long id) {
        ModelAndView editPage = new ModelAndView("/product/edit");

        if (id != null) {
            Product productById = productService.getById(id);

            if (productById != null) {
                editPage.addObject(PRODUCT_ATTRIBUTE, productById);
                editPage.addObject(CATEGORIES_ATTRIBUTE, categoryService.getAllCategories());
            } else {
                return new ModelAndView("redirect:/");
            }
        }

        return editPage;
    }

    @GetMapping("/delete")
    public ModelAndView deletePage() {
        return new ModelAndView("/product/delete");
    }


    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

}
