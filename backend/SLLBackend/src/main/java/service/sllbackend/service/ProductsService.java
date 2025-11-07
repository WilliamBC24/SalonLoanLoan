package service.sllbackend.service;

import java.util.List;

import service.sllbackend.entity.Product;


public interface ProductsService {
    List<Product> getProducts(String name, Boolean activeStatus);

    Product getProductById(Integer id);

    List<Product> getProducts(String query, int page);
    
    Product createProduct(Product product);
    
    Product updateProduct(Integer id, Product product);
    
    Integer getProductStock(Integer productId);
}
