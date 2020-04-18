package com.training.repos;

import com.training.models.Price;
import com.training.models.Product;
import com.training.models.enums.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface PriceRepository extends JpaRepository<Price, Long> {

    List<Price> getByCurrency(Currency currency);

    List<Price> getByProductId(Long productId);

    Price getPriceById(Long id);

    Price getPriceByCurrencyAndProduct(Currency currency, Product product);

    @Query("SELECT p FROM Price p WHERE ((p.currency = :currency) and (p.value >= :price1 and p.value <= :price2))")
    List<Price> getByFromHowMuchMoneyToSoMuchAndCurrency(@RequestParam("price1") Double price1,
                                                         @RequestParam("price2") Double price2,
                                                         @RequestParam("currency") Currency currency);

    @Query("SELECT p FROM Price p WHERE ((p.currency = :currency) and (p.value = :price))")
    List<Price> getByPriceMoneyAndCurrency(@RequestParam("price") Double price,
                                           @RequestParam("currency") Currency currency);


}
