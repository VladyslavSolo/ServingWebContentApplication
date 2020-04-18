package com.training.dto;

import com.training.models.enums.Currency;

import javax.validation.constraints.NotNull;

public class PriceView {

    private Long id;
    @NotNull
    private Long productId;
    @NotNull
    private Double value;
    @NotNull
    private Currency currency;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
