package com.training.models;

import com.training.models.enums.Currency;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "products")
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @OneToMany(mappedBy = "product",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Price> prices = new HashSet<>();
    @ManyToMany
    @JoinTable(name = "product_categories",
            joinColumns = {@JoinColumn(name = "product_id")},
            inverseJoinColumns = {@JoinColumn(name = "categories_id")})
    private Set<Category> categories = new HashSet<>();


    //constructors, getters, setters, overrode methods

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String nameProduct) {
        this.name = nameProduct;
    }

    public void addPrice(Price price) {
        getPrices().add(price);
    }

    public void addCategory(Category category) {
        getCategories().add(category);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Price> getPrices() {
        return prices;
    }


    public Price getPriceByCurrency(Currency currency) {
        for (Price price : this.prices)
            if (currency.equals(price.getCurrency())) {
                return price;
            }
        return null;
    }

    public Double getPriceValueByCurrency(Currency currency) {
        Price priceByCurrency = getPriceByCurrency(currency);
        return priceByCurrency == null ? null : priceByCurrency.getValue();
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public void setPrices(Set<Price> prices) {
        this.prices = prices;
    }

    @Override
    public String toString() {
        return "id=" + id + "; name='" + name + "'";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) &&
                Objects.equals(name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
