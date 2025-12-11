package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import service.sllbackend.entity.OrderInvoice;
import service.sllbackend.enumerator.FulfillmentType;
import service.sllbackend.enumerator.OrderStatus;
import service.sllbackend.service.EmailService;
import service.sllbackend.service.OrderService;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/staff/order")
@RequiredArgsConstructor
public class StaffOrderController {
    
    private final OrderService orderService;
    private final EmailService emailService;

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
            OrderStatus[] orderStatuses;

            if (order.getFulfillmentType() == FulfillmentType.DELIVERY) {
                orderStatuses = Arrays.stream(OrderStatus.values())
                        .filter(s -> s != OrderStatus.READY_FOR_PICKUP
                                && s != OrderStatus.PICKED_UP)
                        .toArray(OrderStatus[]::new);
            } else {
                orderStatuses = Arrays.stream(OrderStatus.values())
                        .filter(s -> s != OrderStatus.SHIPPED
                                && s != OrderStatus.DELIVERED)
                        .toArray(OrderStatus[]::new);
            }

            model.addAttribute("orderStatuses", orderStatuses);
            
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
            if (newStatus == OrderStatus.READY_FOR_PICKUP) {
                emailService.sendOrderPickupNotice(orderId);
            }
            return "redirect:/staff/order/edit?orderId=" + orderId + "&success=Order status updated successfully";
        } catch (Exception e) {
            return "redirect:/staff/order/edit?orderId=" + orderId + "&error=" + e.getMessage();
        }
    }

    @GetMapping("/create")
    public String showCreateOrderForm(Model model) {
        return "staff-create-order";
    }

    @PostMapping("/create")
    @Transactional
    public String createInStoreOrder(
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String username,   // assigned user (optional)
            @RequestParam String itemsJson,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // staff username (who creates the order), can be null if not logged in
            String staffUsername = (principal != null ? principal.getName() : null);

            orderService.createInStoreOrder(
                    staffUsername,        // staff who creates order
                    username,             // optional assigned user (can be null / empty)
                    "aaa",
                    "0999999999",
                    paymentMethod,        // must be IN_STORE or BANK_TRANSFER
                    itemsJson            // optional internal note
            );

            return "redirect:/staff/order/create?success";
        } catch (Exception e) {
            return "redirect:/staff/order/create?error";
        }
    }
}


