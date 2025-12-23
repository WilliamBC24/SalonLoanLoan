package service.sllbackend.web.mvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import service.sllbackend.entity.Product;
import service.sllbackend.entity.Service;
import service.sllbackend.repository.ProductRepo;
import service.sllbackend.service.ProductsService;

@Controller
@RequestMapping("/manager/products")
public class ManagerProductController {

    private final ProductsService productsService;
    private final ProductRepo productRepo;

    public ManagerProductController(ProductsService productsService, ProductRepo productRepo) {
        this.productsService = productsService;
        this.productRepo = productRepo;
    }

    @GetMapping("/list")
    public String listProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean activeStatus,
            Model model) {
        
        List<Product> products = productsService.getProducts(name != null ? name.trim().replaceAll("\\s+", " ") : null, activeStatus);
        
        // Add stock information for each product
        Map<Integer, Integer> stockMap = new HashMap<>();
        for (Product product : products) {
            Integer stock = productsService.getProductStock(product.getId());
            stockMap.put(product.getId(), stock);
        }
        
        model.addAttribute("products", products);
        model.addAttribute("stockMap", stockMap);
        model.addAttribute("searchName", name);
        model.addAttribute("selectedActiveStatus", activeStatus);
        
        return "manager-product-list";
    }

    @GetMapping("/create")
    public String showCreateForm() {
        return "manager-product-create";
    }

    @PostMapping("/create")
    public String createProduct(
            @RequestParam String productName,
            @RequestParam Integer currentPrice,
            @RequestParam String productDescription,
            @RequestParam(required = false, defaultValue = "false") Boolean activeStatus,
            @RequestParam(required = false) String source,
            RedirectAttributes redirectAttributes) {
        
        try {
            Product product = Product.builder()
                .productName(productName)
                .currentPrice(currentPrice)
                .productDescription(productDescription)
                .activeStatus(activeStatus)
                .build();
            
            Product createdProduct = productsService.createProduct(product);
            
            // If called from invoice page, include product ID in redirect
            if ("invoice".equals(source)) {
                redirectAttributes.addFlashAttribute("newProductId", createdProduct.getId());
                redirectAttributes.addFlashAttribute("newProductName", createdProduct.getProductName());
                return "redirect:/manager/invoices/create";
            }
            
            // Redirect to edit page so user can add images
            redirectAttributes.addFlashAttribute("successMessage", "Product created successfully! You can now add images.");
            return "redirect:/manager/products/edit/" + createdProduct.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating product: " + e.getMessage());
            
            if ("invoice".equals(source)) {
                return "redirect:/manager/invoices/create";
            }
            return "redirect:/manager/products/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Product product = productsService.getProductById(id);
        Integer availableStock = productsService.getProductStock(id);
        
        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        
        model.addAttribute("product", product);
        model.addAttribute("availableStock", availableStock);
        return "manager-product-edit";
    }

    @PostMapping("/edit/{id}")
    public String editProduct(
            @PathVariable Integer id,
            @RequestParam String productName,
            @RequestParam Integer currentPrice,
            @RequestParam String productDescription,
            @RequestParam(required = false, defaultValue = "false") Boolean activeStatus,
            RedirectAttributes redirectAttributes) {
        
        try {
            Product product = Product.builder()
                .productName(productName)
                .currentPrice(currentPrice)
                .productDescription(productDescription)
                .activeStatus(activeStatus)
                .build();
            
            productsService.updateProduct(id, product);
            redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully!");
            return "redirect:/manager/products/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating product: " + e.getMessage());
            return "redirect:/manager/products/edit/" + id;
        }
    }
    
    @GetMapping("/api/list")
    @ResponseBody
    public ResponseEntity<List<Product>> getProductsApi() {
        List<Product> products = productsService.getProducts(null, null);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/activate/{id}")
    public String activateProduct(@PathVariable Integer id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setActiveStatus(true);

        productRepo.save(product);
        return "redirect:/manager/products/list";
    }
    @GetMapping("/deactivate/{id}")
    public String deactivateProduct(@PathVariable Integer id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setActiveStatus(false);

        productRepo.save(product);
        return "redirect:/manager/products/list";
    }
}