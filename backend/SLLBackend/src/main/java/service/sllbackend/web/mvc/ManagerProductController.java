package service.sllbackend.web.mvc;

import java.io.IOException;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import service.sllbackend.entity.Product;
import service.sllbackend.entity.ProductImage;
import service.sllbackend.repository.ProductImageRepo;
import service.sllbackend.service.ImageStorageService;
import service.sllbackend.service.ProductsService;

@Controller
@RequestMapping("/manager/products")
@RequiredArgsConstructor
@Slf4j
public class ManagerProductController {

    private final ProductsService productsService;
    private final ImageStorageService imageStorageService;
    private final ProductImageRepo productImageRepo;

    @GetMapping("/list")
    public String listProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean activeStatus,
            Model model) {
        
        List<Product> products = productsService.getProducts(name, activeStatus);
        
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
            @RequestParam(required = false) List<MultipartFile> images,
            RedirectAttributes redirectAttributes) {
        
        try {
            Product product = Product.builder()
                .productName(productName)
                .currentPrice(currentPrice)
                .productDescription(productDescription)
                .activeStatus(activeStatus)
                .build();
            
            Product createdProduct = productsService.createProduct(product);
            
            // Handle image uploads
            if (images != null && !images.isEmpty()) {
                for (MultipartFile image : images) {
                    if (image != null && !image.isEmpty()) {
                        try {
                            String imagePath = imageStorageService.storeImage(image, "products");
                            ProductImage productImage = ProductImage.builder()
                                .product(createdProduct)
                                .imagePath(imagePath)
                                .build();
                            productImageRepo.save(productImage);
                            log.info("Saved product image: {} for product: {}", imagePath, createdProduct.getId());
                        } catch (IOException e) {
                            log.error("Failed to store image for product: {}", createdProduct.getId(), e);
                        }
                    }
                }
            }
            
            // If called from invoice page, include product ID in redirect
            if ("invoice".equals(source)) {
                redirectAttributes.addFlashAttribute("newProductId", createdProduct.getId());
                redirectAttributes.addFlashAttribute("newProductName", createdProduct.getProductName());
                return "redirect:/manager/invoices/create";
            }
            
            redirectAttributes.addFlashAttribute("successMessage", "Product created successfully!");
            return "redirect:/manager/products/list";
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
        List<ProductImage> existingImages = productImageRepo.findByProductId(id);
        
        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        
        model.addAttribute("product", product);
        model.addAttribute("availableStock", availableStock);
        model.addAttribute("existingImages", existingImages);
        return "manager-product-edit";
    }

    @PostMapping("/edit/{id}")
    public String editProduct(
            @PathVariable Integer id,
            @RequestParam String productName,
            @RequestParam Integer currentPrice,
            @RequestParam String productDescription,
            @RequestParam(required = false, defaultValue = "false") Boolean activeStatus,
            @RequestParam(required = false) List<MultipartFile> images,
            @RequestParam(required = false) List<Integer> deleteImageIds,
            RedirectAttributes redirectAttributes) {
        
        try {
            Product product = Product.builder()
                .productName(productName)
                .currentPrice(currentPrice)
                .productDescription(productDescription)
                .activeStatus(activeStatus)
                .build();
            
            Product updatedProduct = productsService.updateProduct(id, product);
            
            // Delete selected images
            if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
                for (Integer imageId : deleteImageIds) {
                    ProductImage existingImage = productImageRepo.findById(imageId).orElse(null);
                    if (existingImage != null && existingImage.getProduct().getId().equals(id)) {
                        imageStorageService.deleteImage(existingImage.getImagePath());
                        productImageRepo.delete(existingImage);
                        log.info("Deleted product image: {} for product: {}", existingImage.getImagePath(), id);
                    }
                }
            }
            
            // Handle new image uploads
            if (images != null && !images.isEmpty()) {
                for (MultipartFile image : images) {
                    if (image != null && !image.isEmpty()) {
                        try {
                            String imagePath = imageStorageService.storeImage(image, "products");
                            ProductImage productImage = ProductImage.builder()
                                .product(updatedProduct)
                                .imagePath(imagePath)
                                .build();
                            productImageRepo.save(productImage);
                            log.info("Saved product image: {} for product: {}", imagePath, id);
                        } catch (IOException e) {
                            log.error("Failed to store image for product: {}", id, e);
                        }
                    }
                }
            }
            
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
}