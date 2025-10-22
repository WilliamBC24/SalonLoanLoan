package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.sllbackend.entity.OrderInvoice;
import service.sllbackend.entity.OrderInvoiceDetails;
import service.sllbackend.enumerator.OrderStatus;
import service.sllbackend.service.impl.OrderServiceImpl;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/staff/order")
@RequiredArgsConstructor
public class StaffOrderController {
    
    private final OrderServiceImpl orderService;

    @GetMapping("/list")
    @Transactional(readOnly = true)
    public String listOrders(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/staff/login";
        }
        
        List<OrderInvoice> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        
        return "staff-order-list";
    }

    @GetMapping("/edit")
    @Transactional(readOnly = true)
    public String editOrderPage(@RequestParam Integer orderId, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/staff/login";
        }
        
        try {
            OrderInvoice order = orderService.getOrderDetails(orderId);
            model.addAttribute("order", order);
            model.addAttribute("orderStatuses", OrderStatus.values());
            
            return "staff-order-edit";
        } catch (Exception e) {
            return "redirect:/staff/order/list?error=" + e.getMessage();
        }
    }

    @PostMapping("/edit")
    @Transactional
    public String updateOrderStatus(
            @RequestParam Integer orderId,
            @RequestParam String orderStatus,
            Principal principal) {
        if (principal == null) {
            return "redirect:/auth/staff/login";
        }
        
        try {
            OrderStatus newStatus = OrderStatus.valueOf(orderStatus);
            orderService.updateOrderStatus(orderId, newStatus);
            return "redirect:/staff/order/list?success=Order status updated successfully";
        } catch (Exception e) {
            return "redirect:/staff/order/edit?orderId=" + orderId + "&error=" + e.getMessage();
        }
    }
}
