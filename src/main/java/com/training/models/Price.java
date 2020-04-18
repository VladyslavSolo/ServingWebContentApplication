package com.training.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.training.models.enums.Currency;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "prices")
public class Price implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    @Column
    private Double value;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;


    //constructors, getters, setters, overrode methods

    public Price() {
    }

    public Price(Currency currency, Double value) {
        this.currency = currency;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double priceMoney) {
        this.value = priceMoney;
    }

    @JsonIgnore
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "id=" + id + "; currency=" + currency + "; value=" + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price = (Price) o;
        return Objects.equals(id, price.id) &&
                currency == price.currency &&
                Objects.equals(value, price.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, currency, value);
    }
}
