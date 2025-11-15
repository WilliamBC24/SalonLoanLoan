package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import service.sllbackend.entity.InventoryInvoice;
import service.sllbackend.entity.Product;
import service.sllbackend.entity.Staff;
import service.sllbackend.entity.StaffAccount;
import service.sllbackend.entity.Supplier;
import service.sllbackend.enumerator.InventoryInvoiceStatus;
import service.sllbackend.repository.ProductRepo;
import service.sllbackend.repository.StaffAccountRepo;
import service.sllbackend.repository.SupplierRepo;
import service.sllbackend.service.InventoryInvoiceService;
import service.sllbackend.web.dto.InventoryInvoiceCreateDTO;
import service.sllbackend.web.dto.InventoryInvoiceItemDTO;
import service.sllbackend.web.dto.InventoryInvoiceListDTO;
import service.sllbackend.web.dto.InventoryInvoiceViewDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff/invoices")
@RequiredArgsConstructor
public class InventoryInvoiceController {
    
    private final InventoryInvoiceService inventoryInvoiceService;
    private final SupplierRepo supplierRepo;
    private final ProductRepo productRepo;
    private final StaffAccountRepo staffAccountRepo;
    
    /**
     * Function 15.6: View Invoice List
     * Function 15.5: Search Invoice By Parameter
     */
    @GetMapping("/list")
    @Secured({"ROLE_STAFF", "ROLE_MANAGER", "ROLE_ADMIN"})
    public String listInvoices(
            @RequestParam(required = false) Integer supplierId,
            @RequestParam(required = false) InventoryInvoiceStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            Model model) {
        
        List<InventoryInvoiceListDTO> invoices;
        
        // If any search parameters are provided, use search; otherwise get all
        if (supplierId != null || status != null || fromDate != null || toDate != null) {
            invoices = inventoryInvoiceService.searchInvoices(supplierId, status, fromDate, toDate);
        } else {
            invoices = inventoryInvoiceService.getAllInvoices();
        }
        
        model.addAttribute("invoices", invoices);
        model.addAttribute("suppliers", supplierRepo.findAll());
        model.addAttribute("statuses", InventoryInvoiceStatus.values());
        model.addAttribute("selectedSupplierId", supplierId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        
        return "staff-invoice-list";
    }
    
    /**
     * Function 15.7: View Invoice Detail
     */
    @GetMapping("/detail/{id}")
    @Secured({"ROLE_STAFF", "ROLE_MANAGER", "ROLE_ADMIN"})
    public String viewInvoiceDetail(@PathVariable Integer id, Model model) {
        try {
            InventoryInvoiceViewDTO invoice = inventoryInvoiceService.getInvoiceDetail(id);
            model.addAttribute("invoice", invoice);
            return "staff-invoice-detail";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/staff/invoices/list";
        }
    }
    
    /**
     * Function 15.1: Request to Create Purchase Invoice - Show Form
     */
    @GetMapping("/create")
    @Secured({"ROLE_STAFF", "ROLE_MANAGER", "ROLE_ADMIN"})
    public String showCreateForm(Model model) {
        List<Supplier> suppliers = supplierRepo.findAll();
        List<Product> products = productRepo.findAll();
        
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("products", products);
        
        return "staff-invoice-create";
    }
    
    /**
     * Function 15.1: Request to Create Purchase Invoice - Submit
     */
    @PostMapping("/create")
    @Secured({"ROLE_STAFF", "ROLE_MANAGER", "ROLE_ADMIN"})
    public String createInvoice(
            @RequestParam Integer supplierId,
            @RequestParam(required = false) String note,
            @RequestParam(value = "productId[]", required = false) List<Integer> productIds,
            @RequestParam(value = "quantity[]", required = false) List<Integer> quantities,
            @RequestParam(value = "unitPrice[]", required = false) List<Integer> unitPrices,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Get current staff
            String username = authentication.getName();
            StaffAccount staffAccount = staffAccountRepo.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Staff account not found"));
            Staff staff = staffAccount.getStaff();
            
            // Build DTO
            List<InventoryInvoiceItemDTO> items = new ArrayList<>();
            if (productIds != null && !productIds.isEmpty()) {
                for (int i = 0; i < productIds.size(); i++) {
                    if (quantities.get(i) > 0 && unitPrices.get(i) > 0) {
                        InventoryInvoiceItemDTO item = InventoryInvoiceItemDTO.builder()
                                .productId(productIds.get(i))
                                .orderedQuantity(quantities.get(i))
                                .unitPrice(unitPrices.get(i))
                                .build();
                        items.add(item);
                    }
                }
            }
            
            InventoryInvoiceCreateDTO dto = InventoryInvoiceCreateDTO.builder()
                    .supplierId(supplierId)
                    .note(note)
                    .items(items)
                    .build();
            
            InventoryInvoice invoice = inventoryInvoiceService.createInvoiceRequest(dto, staff.getId());
            
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Invoice created successfully with ID: " + invoice.getId());
            return "redirect:/staff/invoices/detail/" + invoice.getId();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating invoice: " + e.getMessage());
            return "redirect:/staff/invoices/create";
        }
    }
    
    /**
     * Function 15.4: Edit Invoice Status
     */
    @PostMapping("/edit-status/{id}")
    @Secured({"ROLE_STAFF", "ROLE_MANAGER", "ROLE_ADMIN"})
    public String editInvoiceStatus(
            @PathVariable Integer id,
            @RequestParam InventoryInvoiceStatus status,
            RedirectAttributes redirectAttributes) {
        
        try {
            inventoryInvoiceService.updateInvoiceStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Invoice status updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating status: " + e.getMessage());
        }
        
        return "redirect:/staff/invoices/detail/" + id;
    }
}
