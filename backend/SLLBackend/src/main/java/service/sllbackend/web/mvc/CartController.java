package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.sllbackend.entity.Cart;
import service.sllbackend.service.CartService;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    @Transactional(readOnly = true)
    public String viewCart(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/user/login";
        }
        
        List<Cart> cartItems = cartService.getCartByUser(principal.getName());
        
        // Calculate totals
        int totalPrice = cartItems.stream()
                .mapToInt(item -> item.getProduct().getCurrentPrice() * item.getAmount())
                .sum();
        
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(
            @RequestParam Integer productId,
            @RequestParam(defaultValue = "1") Integer amount,
            Principal principal) {
        if (principal == null) {
            return "redirect:/auth/user/login";
        }
        
        cartService.addProductToCart(principal.getName(), productId, amount);
        return "redirect:/products/" + productId;
    }

    @PostMapping("/remove")
    public String removeFromCart(
            @RequestParam Integer productId,
            Principal principal) {
        if (principal == null) {
            return "redirect:/auth/user/login";
        }
        
        cartService.removeProductFromCart(principal.getName(), productId);
        return "redirect:/cart";
    }

    @PostMapping("/adjust")
    public String adjustAmount(
            @RequestParam Integer productId,
            @RequestParam Integer amount,
            Principal principal) {
        if (principal == null) {
            return "redirect:/auth/user/login";
        }
        
        cartService.adjustProductAmount(principal.getName(), productId, amount);
        return "redirect:/cart";
    }

    @PostMapping("/api/update-quantity")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, Object>> updateQuantity(
            @RequestParam Integer productId,
            @RequestParam Integer amount,
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        
        cartService.adjustProductAmount(principal.getName(), productId, amount);
        
        // Recalculate totals
        List<Cart> cartItems = cartService.getCartByUser(principal.getName());
        int totalPrice = cartItems.stream()
                .mapToInt(item -> item.getProduct().getCurrentPrice() * item.getAmount())
                .sum();
        
        // Find the updated item to return its new total
        Cart updatedItem = cartItems.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("totalPrice", totalPrice);
        if (updatedItem != null) {
            response.put("itemTotal", updatedItem.getProduct().getCurrentPrice() * updatedItem.getAmount());
        }
        
        return ResponseEntity.ok(response);
    }
}
