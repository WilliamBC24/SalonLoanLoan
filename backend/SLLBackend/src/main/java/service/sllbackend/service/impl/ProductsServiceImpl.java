package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.sllbackend.entity.Product;
import service.sllbackend.repository.ProductRepo;
import service.sllbackend.service.ProductsService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductsServiceImpl implements ProductsService {
    private final ProductRepo productRepo;

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProducts(String name, Boolean activeStatus) {
        if ((name != null && !name.trim().isEmpty()) || activeStatus != null) {
            String pattern = (name != null && !name.trim().isEmpty()) ? "%" + name.trim() + "%" : null;
            return productRepo.searchProducts(pattern, activeStatus);
        } else {
            return productRepo.findAllProducts(PageRequest.of(0, 10));
        }
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
    
    @Override
    @Transactional
    public void deleteProduct(Integer id) {
        Product product = productRepo.findProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        productRepo.delete(product);
    }
}
