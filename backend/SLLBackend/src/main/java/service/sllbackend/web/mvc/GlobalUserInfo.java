package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import service.sllbackend.service.CartService;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalUserInfo {
    private final CartService cartService;

    @ModelAttribute("username")
    public String username(Authentication authentication) {
        return (authentication != null) ? authentication.getName() : null;
    }

    @ModelAttribute("cartCount")
    public int cartCount(Authentication authentication) {
        return (authentication != null) ? cartService.getCartCount(authentication.getName()) : 0;
    }
}
