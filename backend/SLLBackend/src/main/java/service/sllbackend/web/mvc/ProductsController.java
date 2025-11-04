package service.sllbackend.web.mvc;

import java.security.Principal;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import service.sllbackend.entity.Product;
import service.sllbackend.entity.ProductFeedback;
import service.sllbackend.service.impl.ProductsServiceImpl;
import service.sllbackend.service.impl.ProductFeedbackServiceImpl;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductsController {
    private final ProductsServiceImpl productsService;
    private final ProductFeedbackServiceImpl productFeedbackService;

    @GetMapping
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

    @GetMapping("/{id}")
    public String viewProductDetails(@PathVariable Integer id, Model model, Principal principal) {
        Product product = productsService.getProductById(id);

        model.addAttribute("product", product);
        
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
}
