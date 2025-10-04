package service.sllbackend.web.mvc;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import service.sllbackend.entity.Product;
import service.sllbackend.repository.ProductRepo;

@Controller
@RequestMapping("/")
public class ProductsController {

    private final ProductRepo productRepo;

    public ProductsController(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    @GetMapping("products")
    @Transactional(readOnly = true)
    public String listProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean activeStatus,
            Model model) {
        
        // Get products based on filters
        List<Product> products;
        if ((name != null && !name.trim().isEmpty()) || activeStatus != null) {
            String pattern = (name != null && !name.trim().isEmpty()) ? "%" + name.trim() + "%" : null;
            products = productRepo.searchProducts(pattern, activeStatus);
        } else {
            products = productRepo.findAllProducts();
        }
        
        model.addAttribute("products", products);
        model.addAttribute("searchName", name);
        model.addAttribute("selectedActiveStatus", activeStatus);
        
        return "products";
    }

    @GetMapping("products/{id}")
    @Transactional(readOnly = true)
    public String viewProductDetails(@PathVariable Integer id, Model model) {
        Product product = productRepo.findProductById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        model.addAttribute("product", product);
        return "product-details";
    }
}
