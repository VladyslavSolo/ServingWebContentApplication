package com.training.utils;

import com.training.models.Category;
import com.training.models.Price;
import com.training.models.Product;
import com.training.models.enums.Currency;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class DataGenerator {

    public List<Category> generateCategories() {
        Category pants = new Category("pants");
        Category shirts = new Category("shirts");
        Category skirts = new Category("skirts");
        Category dresses = new Category("dresses");
        Category winterCoat = new Category("winter coat");
        Category summerJacket = new Category("summer jacket");

        Category men = new Category("men's");
        Category women = new Category("women's");
        Category winter = new Category("winter");
        Category summer = new Category("summer");

        Category clothes = new Category("clothes");

        fillCategory(clothes, Arrays.asList(men, women, winter, summer));
        fillCategory(men, Arrays.asList(pants, shirts));
        fillCategory(women, Arrays.asList(skirts, dresses));
        fillCategory(winter, Collections.singletonList(winterCoat));
        fillCategory(summer, Collections.singletonList(summerJacket));

        return Arrays.asList(pants, shirts, skirts, dresses, winterCoat,
                summerJacket, men, women, winter, summer, clothes);
    }

    private void fillCategory(Category targetCategory,
                              @NotNull List<Category> subCategories) {
        subCategories.forEach(subCategory -> {
            targetCategory.addChild(subCategory);
            subCategory.setParent(targetCategory);
        });
    }

    public List<Product> generateProducts(int quantityProducts,
                                          @NotNull List<Category> categories) {
        int quantityCategories = categories.size();
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < quantityProducts; i++) {
            List<Price> prices = generatePrices();
            Product product = new Product();
            product.setName("Product " + i);
            prices.forEach(price -> price.setProduct(product));
            product.getPrices().addAll(prices);
            product.addCategory(categories.get(randomValue(quantityCategories)));
            products.add(product);
        }
        return products;
    }

    @NotNull
    private List<Price> generatePrices() {
        int quantityCurrency = randomValue(3);
        List<Price> prices = new ArrayList<>();
        for (int i = 0; i < quantityCurrency + 1; i++) {
            prices.add(new Price(Currency.values()[i], ((Math.ceil(Math.random() * 1000 * 100)) / 100)));
        }
        return prices;
    }

    public int randomValue(int quantity) {
        return (int) Math.round(Math.random() * (quantity - 1));
    }

}
