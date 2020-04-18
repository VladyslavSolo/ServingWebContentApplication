package com.training.services;

import com.training.dto.PriceView;
import com.training.models.Price;
import com.training.models.Product;
import com.training.models.enums.Currency;

import java.util.List;

public interface PriceService {

    List<Price> getPricesByCurrency(Currency currency);

    List<Price> getPricesByProductId(Long productId);

    List<Price> getPricesByPriceRange(Double from, Double to, Currency currency);

    Price getPriceByCurrencyAndProduct(Currency currency, Product product);

    void deletePriceByCurrencyAndProduct(Currency currency, Product product);

    Price saveOrUpdate(PriceView priceView);

    Price update(Price price);

    Price save(Price price);

    Price transformDtoToModel(PriceView priceView);
}
