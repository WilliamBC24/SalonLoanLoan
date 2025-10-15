package service.sllbackend.web.mvc;

import java.util.List;
import java.util.ArrayList;
import java.security.Principal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import service.sllbackend.entity.Product;
import service.sllbackend.service.impl.ProductsServiceImpl;
import service.sllbackend.web.dto.ProductWithImageDto;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class ProductsController {
    private final ProductsServiceImpl productsService;

    @GetMapping("products")
    public String listProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean activeStatus,
            Model model,
            Principal principal) {
        
        // Truyền thông tin user đang đăng nhập
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }
        
        List<Product> products = productsService.getProducts(name, activeStatus);
        
        // Create list of products with their images
        List<ProductWithImageDto> productsWithImages = new ArrayList<>();
        for (Product product : products) {
            String imagePath = productsService.getProductImagePath(product.getId());
            productsWithImages.add(ProductWithImageDto.builder()
                    .product(product)
                    .imagePath(imagePath)
                    .build());
        }

        model.addAttribute("products", productsWithImages);
        model.addAttribute("searchName", name);
        model.addAttribute("selectedActiveStatus", activeStatus);
        
        return "products";
    }

    @GetMapping("products/{id}")
    public String viewProductDetails(@PathVariable Integer id, Model model) {
        Product product = productsService.getProductById(id);
        String imagePath = productsService.getProductImagePath(id);

        model.addAttribute("product", product);
        model.addAttribute("imagePath", imagePath);
        return "product-details";
    }

    @GetMapping("api/products/{id}")
    @ResponseBody
    public ProductWithImageDto getProductApi(@PathVariable Integer id) {
        Product product = productsService.getProductById(id);
        String imagePath = productsService.getProductImagePath(id);
        
        return ProductWithImageDto.builder()
                .product(product)
                .imagePath(imagePath)
                .build();
    }
}
