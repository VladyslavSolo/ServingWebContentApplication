package com.training.services;

import com.training.dto.ProductView;
import com.training.models.Product;
import com.training.models.enums.Currency;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    List<Product> getProductByName(String name);

    List<Product> getProductsByPrice(Currency currency, Double price);

    List<Product> getProductsByCategoryId(Long categoryId);

    Product saveOrUpdate(Product product);

    Product getById(Long id);

    void delete(Product product);

    Page<Product> getAllProducts(Integer pageNumber);

    List<Product> find(ProductView productView);
}
