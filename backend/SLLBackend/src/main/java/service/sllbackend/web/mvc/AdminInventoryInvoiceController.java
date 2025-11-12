package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import service.sllbackend.enumerator.InventoryInvoiceStatus;
import service.sllbackend.service.InventoryInvoiceService;
import service.sllbackend.web.dto.InventoryInvoiceListDTO;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/invoices")
@RequiredArgsConstructor
public class AdminInventoryInvoiceController {
    
    private final InventoryInvoiceService inventoryInvoiceService;
    
    /**
     * View pending invoices for approval
     */
    @GetMapping("/pending")
    @Secured("ROLE_ADMIN")
    public String viewPendingInvoices(Model model) {
        List<InventoryInvoiceListDTO> allInvoices = inventoryInvoiceService.getAllInvoices();
        List<InventoryInvoiceListDTO> pendingInvoices = allInvoices.stream()
                .filter(invoice -> invoice.getInvoiceStatus() == InventoryInvoiceStatus.AWAITING)
                .collect(Collectors.toList());
        
        model.addAttribute("invoices", pendingInvoices);
        return "admin-invoice-pending";
    }
    
    /**
     * Function 15.2: Approve Invoice Purchase Request
     */
    @PostMapping("/approve/{id}")
    @Secured("ROLE_ADMIN")
    public String approveInvoice(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {
        
        try {
            inventoryInvoiceService.approveInvoice(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Invoice approved successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Error approving invoice: " + e.getMessage());
        }
        
        return "redirect:/admin/invoices/pending";
    }
    
    /**
     * Function 15.3: Reject Invoice Purchase Request
     */
    @PostMapping("/reject/{id}")
    @Secured("ROLE_ADMIN")
    public String rejectInvoice(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {
        
        try {
            inventoryInvoiceService.rejectInvoice(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Invoice rejected successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Error rejecting invoice: " + e.getMessage());
        }
        
        return "redirect:/admin/invoices/pending";
    }
}
