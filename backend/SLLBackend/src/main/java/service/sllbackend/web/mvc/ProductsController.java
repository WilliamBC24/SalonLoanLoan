package service.sllbackend.web.mvc;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import service.sllbackend.entity.Product;
import service.sllbackend.service.impl.ProductsServiceImpl;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class ProductsController {
    private final ProductsServiceImpl productsService;

    @GetMapping("products")
    public String listProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean activeStatus,
            Model model) {
        List<Product> products = productsService.getProducts(name, activeStatus);

        model.addAttribute("products", products);
        model.addAttribute("searchName", name);
        model.addAttribute("selectedActiveStatus", activeStatus);
        
        return "products";
    }

    @GetMapping("products/{id}")
    public String viewProductDetails(@PathVariable Integer id, Model model) {
        Product product = productsService.getProductById(id);

        model.addAttribute("product", product);
        return "product-details";
    }
}
