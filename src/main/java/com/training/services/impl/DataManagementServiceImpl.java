package com.training.services.impl;

import com.training.models.Category;
import com.training.models.Product;
import com.training.repos.CategoryRepository;
import com.training.repos.PriceRepository;
import com.training.repos.ProductRepository;
import com.training.services.DataManagementService;
import com.training.utils.DataGenerator;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class DataManagementServiceImpl implements DataManagementService {

    private final DataGenerator dataGenerator;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PriceRepository priceRepository;

    @Override
    public List<Product> generateData() {
        clearData();
        List<Category> categories = dataGenerator.generateCategories();
        List<Product> products = dataGenerator.generateProducts(100, categories);
        categoryRepository.saveAll(categories);
        return productRepository.saveAll(products);
    }

    @Override
    public void clearData() {
        categoryRepository.deleteAll();
        productRepository.deleteAll();
        priceRepository.deleteAll();
    }

    public DataManagementServiceImpl(DataGenerator dataGenerator,
                                     CategoryRepository categoryRepository,
                                     ProductRepository productRepository,
                                     PriceRepository priceRepository) {
        this.dataGenerator = dataGenerator;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.priceRepository = priceRepository;
    }

}
