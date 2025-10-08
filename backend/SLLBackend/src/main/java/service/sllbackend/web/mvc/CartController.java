package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.sllbackend.entity.Cart;
import service.sllbackend.service.impl.CartServiceImpl;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartServiceImpl cartService;

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
        return "redirect:/cart";
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
}
