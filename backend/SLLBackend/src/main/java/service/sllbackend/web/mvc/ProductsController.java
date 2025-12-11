package service.sllbackend.web.mvc;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import service.sllbackend.entity.Product;
import service.sllbackend.entity.ProductFeedback;
import service.sllbackend.service.ProductFeedbackService;
import service.sllbackend.service.ProductsService;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductsController {
    private final ProductsService productsService;
    private final ProductFeedbackService productFeedbackService;

    @GetMapping
    public String listProducts(
            @RequestParam(required = false) String name,
            Model model) {
        List<Product> products = productsService.getProducts(name, true);

        model.addAttribute("products", products);
        model.addAttribute("searchName", name);
        
        return "products";
    }

    @GetMapping("/{id}")
    public String viewProductDetails(@PathVariable Integer id, Model model, Principal principal) {
        Product product = productsService.getProductById(id);
        Integer availableStock = productsService.getProductStock(id);

        model.addAttribute("product", product);
        model.addAttribute("availableStock", availableStock);
        
        // Get all product feedback
        List<ProductFeedback> feedbackList = productFeedbackService.getProductFeedback(id);
        model.addAttribute("feedbackList", feedbackList);
        
        // Calculate average rating
        if (!feedbackList.isEmpty()) {
            double averageRating = feedbackList.stream()
                    .mapToInt(ProductFeedback::getRating)
                    .average()
                    .orElse(0.0);
            model.addAttribute("averageRating", averageRating);
            model.addAttribute("totalReviews", feedbackList.size());
        } else {
            model.addAttribute("averageRating", 0.0);
            model.addAttribute("totalReviews", 0);
        }
        
        // Check if user is logged in and can rate
        boolean isLoggedIn = principal != null;
        model.addAttribute("isLoggedIn", isLoggedIn);
        
        if (principal != null) {
            boolean canRate = productFeedbackService.canUserRateProduct(principal.getName(), id);
            model.addAttribute("canRate", canRate);
            
            // Get user's existing feedback if any
            ProductFeedback userFeedback = productFeedbackService.getUserFeedback(principal.getName(), id);
            model.addAttribute("userFeedback", userFeedback);
        } else {
            model.addAttribute("canRate", false);
        }
        
        return "product-details";
    }
    @GetMapping("/search")
    @ResponseBody
    public List<Map<String, Object>> searchProducts(@RequestParam String query) {

        // Get list of products that match the name/code
        List<Product> products = productsService.getProducts(query, true);

        // Return minimal JSON objects for the AJAX frontend
        return products.stream()
                .map(p -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("id", p.getId());
                    dto.put("productName", p.getProductName());
                    dto.put("currentPrice", p.getCurrentPrice());
                    return dto;
                })
                .toList();
    }

}
