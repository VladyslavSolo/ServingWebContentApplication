package com.training.services.impl;

import com.training.dto.PriceView;
import com.training.models.Price;
import com.training.models.Product;
import com.training.models.enums.Currency;
import com.training.repos.PriceRepository;
import com.training.repos.ProductRepository;
import com.training.services.PriceService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class PriceServiceImpl implements PriceService {

    public final PriceRepository priceRepository;
    private final ProductRepository productRepository;

    @Override
    public List<Price> getPricesByCurrency(Currency currency) {
        return priceRepository.getByCurrency(currency);
    }

    @Override
    public List<Price> getPricesByProductId(Long productId) {
        return priceRepository.getByProductId(productId);
    }

    @Override
    public List<Price> getPricesByPriceRange(Double from, Double to, Currency currency) {
        return priceRepository.getByFromHowMuchMoneyToSoMuchAndCurrency(from, to, currency);
    }

    @Override
    public Price getPriceByCurrencyAndProduct(Currency currency, Product product) {
        return priceRepository.getPriceByCurrencyAndProduct(currency, product);
    }

    @Override
    public Price saveOrUpdate(PriceView priceView) {
        Price transformedPrice = transformDtoToModel(priceView);
        Price result = null;

        if (transformedPrice != null) {
            if (transformedPrice.getId() != null) {
                result = update(transformedPrice);
            } else {
                result = save(transformedPrice);
            }
        }
        return result;
    }

    @Override
    public Price save(@NotNull Price price) {
        Product product = price.getProduct();
        Currency currency = price.getCurrency();

        deletePriceByCurrencyAndProduct(currency, product);
        product.addPrice(price);
        productRepository.save(product);
        return priceRepository.getPriceByCurrencyAndProduct(currency, product);
    }

    @Override
    public Price update(@NotNull Price price) {
        Price result = null;
        Product product = price.getProduct();

        if (product != null) {
            Currency currency = price.getCurrency();
            Price priceByProduct = getPriceByCurrencyAndProduct(currency, product);

            if (priceByProduct != null) {

                if (price.getId().equals(priceByProduct.getId())) {
                    result = priceRepository.save(price);

                } else {
                    deletePriceByCurrencyAndProduct(currency, product);
                    product.addPrice(price);
                    productRepository.save(product);
                    result = getPriceByCurrencyAndProduct(currency, product);
                }
            }
        }
        return result;
    }

    @Override
    public void deletePriceByCurrencyAndProduct(@NotNull Currency currency, @NotNull Product product) {
        Set<Price> prices = product.getPrices();
        prices.stream().filter(price -> price.getCurrency()
                .equals(currency)).findFirst().ifPresent(prices::remove);
    }

    @Nullable
    @Override
    public Price transformDtoToModel(@NotNull PriceView priceView) {
        Price price = null;
        Product product = productRepository.getById(priceView.getProductId());

        if (product != null) {
            price = new Price();
            price.setProduct(product);
            price.setId(priceView.getId());
            price.setValue(priceView.getValue());
            Currency currency = priceView.getCurrency();
            price.setCurrency(currency);
        }
        return price;
    }

    public PriceServiceImpl(PriceRepository priceRepository, ProductRepository productRepository) {
        this.priceRepository = priceRepository;
        this.productRepository = productRepository;
    }

}
