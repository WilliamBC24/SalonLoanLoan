package service.sllbackend.web.mvc;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import service.sllbackend.entity.Product;
import service.sllbackend.service.ProductsService;

@Controller
@RequestMapping("/staff/products")
public class StaffProductController {

    private final ProductsService productsService;

    public StaffProductController(ProductsService productsService) {
        this.productsService = productsService;
    }

    @GetMapping("/list")
    public String listProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean activeStatus,
            Model model) {
        
        List<Product> products = productsService.getProducts(name, activeStatus);
        
        model.addAttribute("products", products);
        model.addAttribute("searchName", name);
        model.addAttribute("selectedActiveStatus", activeStatus);
        
        return "staff-product-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        return "staff-product-create";
    }

    @PostMapping("/create")
    public String createProduct(
            @RequestParam String productName,
            @RequestParam Integer currentPrice,
            @RequestParam String productDescription,
            @RequestParam(required = false, defaultValue = "0") Integer stock,
            @RequestParam(required = false, defaultValue = "false") Boolean activeStatus,
            RedirectAttributes redirectAttributes) {
        
        try {
            Product product = Product.builder()
                .productName(productName)
                .currentPrice(currentPrice)
                .productDescription(productDescription)
                .stock(stock)
                .activeStatus(activeStatus)
                .build();
            
            productsService.createProduct(product);
            redirectAttributes.addFlashAttribute("successMessage", "Product created successfully!");
            return "redirect:/staff/products/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating product: " + e.getMessage());
            return "redirect:/staff/products/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Product product = productsService.getProductById(id);
        
        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        
        model.addAttribute("product", product);
        return "staff-product-edit";
    }

    @PostMapping("/edit/{id}")
    public String editProduct(
            @PathVariable Integer id,
            @RequestParam String productName,
            @RequestParam Integer currentPrice,
            @RequestParam String productDescription,
            @RequestParam(required = false, defaultValue = "0") Integer stock,
            @RequestParam(required = false, defaultValue = "false") Boolean activeStatus,
            RedirectAttributes redirectAttributes) {
        
        try {
            Product product = Product.builder()
                .productName(productName)
                .currentPrice(currentPrice)
                .productDescription(productDescription)
                .stock(stock)
                .activeStatus(activeStatus)
                .build();
            
            productsService.updateProduct(id, product);
            redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully!");
            return "redirect:/staff/products/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating product: " + e.getMessage());
            return "redirect:/staff/products/edit/" + id;
        }
    }
}