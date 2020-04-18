package com.training.services.impl;

import com.training.dto.ProductView;
import com.training.models.Category;
import com.training.models.Price;
import com.training.models.Product;
import com.training.models.enums.Currency;
import com.training.repos.CategoryRepository;
import com.training.repos.PriceRepository;
import com.training.repos.ProductRepository;
import com.training.services.PriceService;
import com.training.services.ProductService;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final PriceRepository priceRepository;
    private final PriceService priceService;

    @Override
    public List<Product> getProductByName(String name) {
        return productRepository.getByName(name);
    }

    @Override
    public List<Product> getProductsByPrice(Currency currency, Double price) {
        List<Price> prices = priceRepository.getByPriceMoneyAndCurrency(price, currency);
        return prices.stream().map(Price::getProduct).collect(Collectors.toList());
    }

    @Override
    public List<Product> getProductsByCategoryId(Long categoryId) {
        List<Product> products = null;
        Category categoryById = null;

        if (categoryId != null) {
            categoryById = categoryRepository.getById(categoryId);
        }
        if (categoryById != null) {
            products = new ArrayList<>(categoryById.getProducts());
        }

        return products;
    }

    @Override
    public Product saveOrUpdate(Product product) {
        Product result = null;

        if (product != null) {
            if (product.getId() != null) {
                result = update(product);
            } else {
                result = save(product);
            }
        }
        return result;
    }

    private Product save(@NotNull Product product) {
        Set<Category> categories = product.getCategories();
        Set<Price> prices = product.getPrices();

        if (categories != null && !categories.isEmpty()) {
            List<Long> categoryIds = categories.stream()
                    .map(Category::getId).collect(Collectors.toList());
            categoryRepository.getCategoriesByIds(categoryIds);
            categories.forEach(category -> category.getProducts().add(product));
            prices.forEach(price -> price.setProduct(product));
            product.setCategories(categories);
        }
        return productRepository.save(product);
    }


    private Product update(@NotNull Product product) {
        Product result = null;
        Long id = product.getId();
        String name = product.getName();
        Set<Category> categories = product.getCategories();
        Set<Price> prices = product.getPrices();

        if (id != null) {
            Product productById = productRepository.getById(id);

            if (productById == null) {
                return null;
            }
            if (prices != null && !prices.isEmpty()) {
                Set<Price> preparedPrices = new HashSet<>(); //2

                prices.forEach(price -> {
                    price.setProduct(productById);
                    preparedPrices.add(price); //2
                    priceService.save(price); //1
                });
                productById.getPrices().clear(); //2
                productById.getPrices().addAll(preparedPrices); //2
            }
            if (name != null) {
                productById.setName(name);
            }
            if (categories != null && !categories.isEmpty()) {
                List<Long> categoryIds = categories.stream()
                        .map(Category::getId).collect(Collectors.toList());

                categories = categoryRepository.getCategoriesByIds(categoryIds);
                categories.forEach(category -> category.getProducts().add(productById)); //check fetching
                productById.setCategories(categories);
            }

            result = productRepository.save(productById);
        }
        return result;
    }

    @Override
    public Product getById(Long id) {
        return productRepository.getById(id);
    }

    @Override
    public void delete(Product product) {
        productRepository.delete(product);
    }

    @Override
    public Page<Product> getAllProducts(Integer pageNumber) {
        return productRepository.findAll(PageRequest.of(pageNumber, 10));
    }

    @Override
    public List<Product> find(@NotNull ProductView productView) {
        List<Product> result = new ArrayList<>();
        Long id = productView.getId();
        String name = productView.getName();
        Long categoryId = productView.getCategoryId();
        Double price = productView.getPrice();
        Currency currency = productView.getCurrency();

        if (id != null) {
            Product product = getById(id);
            if (product != null) {
                result.add(product);
            }
        } else if (name != null) {
            result = getProductByName(name.trim());
        } else if (categoryId != null) {
            result = getProductsByCategoryId(categoryId);
        } else if (price != null && currency != null) {
            result = getProductsByPrice(currency, price);
        }

        return result;
    }

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              PriceRepository priceRepository,
                              PriceService priceService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.priceRepository = priceRepository;
        this.priceService = priceService;
    }

}
