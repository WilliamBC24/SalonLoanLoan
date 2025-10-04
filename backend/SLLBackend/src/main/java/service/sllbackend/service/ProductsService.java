package service.sllbackend.service;

import service.sllbackend.entity.Product;

import java.util.List;


public interface ProductsService {
    List<Product> getProducts(String name, Boolean activeStatus);

    Product getProductById(Integer id);

    List<Product> getTenProducts();
}
