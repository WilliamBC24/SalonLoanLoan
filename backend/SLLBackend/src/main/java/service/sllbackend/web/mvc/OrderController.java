package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.sllbackend.entity.OrderInvoice;
import service.sllbackend.entity.OrderInvoiceDetails;
import service.sllbackend.enumerator.OrderStatus;
import service.sllbackend.service.impl.OrderServiceImpl;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderServiceImpl orderService;

    @GetMapping("/checkout")
    @Transactional(readOnly = true)
    public String checkoutPage(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/user/login";
        }
        
        Map<String, Object> cartSummary = orderService.getCartSummary(principal.getName());
        
        // Redirect to cart if empty
        if ((int) cartSummary.get("itemCount") == 0) {
            return "redirect:/cart";
        }
        
        model.addAttribute("cartSummary", cartSummary);
        
        return "order-checkout";
    }

    @PostMapping("/checkout")
    @Transactional
    public String placeOrder(
            @RequestParam String customerName,
            @RequestParam String phoneNumber,
            @RequestParam String shippingAddress,
            @RequestParam String paymentMethod,
            Principal principal) {
        if (principal == null) {
            return "redirect:/auth/user/login";
        }
        
        try {
            OrderInvoice order = orderService.placeOrder(
                    principal.getName(),
                    customerName,
                    phoneNumber,
                    shippingAddress,
                    paymentMethod
            );
            
            return "redirect:/order/details?orderId=" + order.getId();
        } catch (Exception e) {
            return "redirect:/order/checkout?error=" + e.getMessage();
        }
    }

    @GetMapping("/history")
    @Transactional(readOnly = true)
    public String orderHistory(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/user/login";
        }
        
        System.out.println(principal.getName());
        List<OrderInvoice> orders = orderService.getOrderHistory(principal.getName());
        model.addAttribute("orders", orders);
        
        return "order-history";
    }

    @GetMapping("/details")
    @Transactional(readOnly = true)
    public String orderDetails(@RequestParam Integer orderId, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/user/login";
        }
        
        try {
            OrderInvoice order = orderService.getOrderDetails(orderId);
            List<OrderInvoiceDetails> orderItems = orderService.getOrderItems(orderId);
            
            model.addAttribute("order", order);
            model.addAttribute("orderItems", orderItems);
            
            return "order-details";
        } catch (Exception e) {
            return "redirect:/order/history?error=" + e.getMessage();
        }
    }

    @GetMapping("/cancel")
    @Transactional
    public String cancelOrder(@RequestParam Integer orderId, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/user/login";
        }
        
        try {
            orderService.cancelOrder(orderId, principal.getName());
            return "redirect:/order/details?orderId=" + orderId + "&success=Order cancelled successfully";
        } catch (Exception e) {
            return "redirect:/order/details?orderId=" + orderId + "&error=" + e.getMessage();
        }
    }

    // API endpoint for AJAX requests
    @PostMapping("/api/cancel")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, Object>> cancelOrderApi(
            @RequestParam Integer orderId,
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        
        try {
            orderService.cancelOrder(orderId, principal.getName());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Order cancelled successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
