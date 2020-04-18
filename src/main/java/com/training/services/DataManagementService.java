package com.training.services;

import com.training.models.Product;

import java.util.List;

public interface DataManagementService {

    List<Product> generateData();

    void clearData();

}
