package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import service.sllbackend.service.CartService;
import service.sllbackend.service.UserAccountService;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalUserInfo {
    private final CartService cartService;
    private final UserAccountService userAccountService;

    @ModelAttribute("username")
    public String username(Authentication authentication) {
        return (authentication != null) ? authentication.getName() : null;
    }

    @ModelAttribute("userPhoneNumber")
    public String phoneNumber(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        boolean isStaff = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STAFF") ||
                        a.getAuthority().equals("ROLE_ADMIN"));

        if (isStaff) {
            return null;
        }
        return userAccountService.findByUsername(authentication.getName()).getPhoneNumber();
    }


    @ModelAttribute("cartCount")
    public int cartCount(Authentication authentication) {
        return (authentication != null) ? cartService.getCartCount(authentication.getName()) : 0;
    }
}
