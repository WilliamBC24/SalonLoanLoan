package service.sllbackend.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import service.sllbackend.entity.Product;
import service.sllbackend.repository.ProductRepo;
import service.sllbackend.service.ProductsService;

@Service
@RequiredArgsConstructor
public class ProductsServiceImpl implements ProductsService {
    private final ProductRepo productRepo;

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProducts(String name, Boolean activeStatus) {
        List<Product> allProducts = productRepo.findAll();
        List<Product> filteredByName = new ArrayList<>();

        if (name != null && !name.trim().isEmpty()) {
            String lowerCaseName = name.trim().toLowerCase();
            for (Product product : allProducts) {
                if (product.getProductName() != null && product.getProductName().toLowerCase().contains(lowerCaseName)) {
                    filteredByName.add(product);
                }
            }
        } else {
            filteredByName = allProducts;
        }

        if (activeStatus != null) {
            filteredByName.removeIf(product -> !activeStatus.equals(product.getActiveStatus()));
        }

        return filteredByName;
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Integer id) {
        return productRepo.findProductById(id).orElse(null);
    }

    @Override
    public List<Product> getTenProducts() {
        return productRepo.findAllProducts(PageRequest.of(0, 10));
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        return productRepo.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(Integer id, Product product) {
        Product existingProduct = productRepo.findProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        existingProduct.setProductName(product.getProductName());
        existingProduct.setCurrentPrice(product.getCurrentPrice());
        existingProduct.setProductDescription(product.getProductDescription());
        existingProduct.setActiveStatus(product.getActiveStatus());

        return productRepo.save(existingProduct);
    }
}