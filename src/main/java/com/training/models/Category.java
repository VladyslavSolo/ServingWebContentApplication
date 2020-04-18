package com.training.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "categories")
public class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @ManyToOne
    private Category parent;
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private Set<Category> children = new HashSet<>();
    @ManyToMany(mappedBy = "categories")
    private Set<Product> products = new HashSet<>();

    //constructors, getters, setters, overrode methods

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    public void addChild(Category category) {
        getChildren().add(category);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParent(Category superCategory) {
        this.parent = superCategory;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Category getParent() {
        return parent;
    }

    @JsonIgnore
    public Set<Category> getChildren() {
        return children;
    }

    @JsonIgnore
    public Set<Product> getProducts() {
        return products;
    }

    @Override
    public String toString() {
        return "id=" + id + "; name='" + name + '\'';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id) &&
                Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}


