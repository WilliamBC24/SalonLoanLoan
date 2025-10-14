package service.sllbackend.web.mvc;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import service.sllbackend.entity.Supplier;
import service.sllbackend.entity.SupplierCategory;
import service.sllbackend.repository.SupplierCategoryRepo;
import service.sllbackend.service.SupplierService;

@Controller
@RequestMapping("/staff/supplier")
public class StaffProviderController {

    private final SupplierService supplierService;
    private final SupplierCategoryRepo supplierCategoryRepo;

    public StaffProviderController(SupplierService supplierService, SupplierCategoryRepo supplierCategoryRepo) {
        this.supplierService = supplierService;
        this.supplierCategoryRepo = supplierCategoryRepo;
    }

    @GetMapping("/list")
    public String listProviders(
            @RequestParam(required = false) List<Integer> categories,
            @RequestParam(required = false) String name,
            Model model) {
        
        // Get all categories for filters
        List<SupplierCategory> allCategories = supplierCategoryRepo.findAll();
        model.addAttribute("allCategories", allCategories);
        
        // Get suppliers based on filters
        List<Supplier> suppliers = supplierService.getSuppliers(categories, name);
        
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("selectedCategories", categories);
        model.addAttribute("searchName", name);
        
        return "staff-provider-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        List<SupplierCategory> categories = supplierCategoryRepo.findAll();
        model.addAttribute("categories", categories);
        return "staff-provider-create";
    }

    @PostMapping("/create")
    public String createProvider(
            @RequestParam String supplierName,
            @RequestParam Integer supplierCategoryId,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String note,
            RedirectAttributes redirectAttributes) {
        
        try {
            SupplierCategory category = supplierCategoryRepo.findById(supplierCategoryId)
                .orElseThrow(() -> new RuntimeException("Supplier category not found"));
            
            Supplier supplier = Supplier.builder()
                .supplierName(supplierName)
                .supplierCategory(category)
                .phoneNumber(phoneNumber)
                .email(email)
                .note(note)
                .build();
            
            supplierService.createSupplier(supplier);
            redirectAttributes.addFlashAttribute("successMessage", "Provider created successfully!");
            return "redirect:/staff/supplier/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating provider: " + e.getMessage());
            return "redirect:/staff/supplier/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Supplier supplier = supplierService.getSupplierById(id);
        
        if (supplier == null) {
            throw new RuntimeException("Provider not found");
        }
        
        List<SupplierCategory> categories = supplierCategoryRepo.findAll();
        model.addAttribute("supplier", supplier);
        model.addAttribute("categories", categories);
        return "staff-provider-edit";
    }

    @PostMapping("/edit/{id}")
    public String editProvider(
            @PathVariable Integer id,
            @RequestParam String supplierName,
            @RequestParam Integer supplierCategoryId,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String note,
            RedirectAttributes redirectAttributes) {
        
        try {
            SupplierCategory category = supplierCategoryRepo.findById(supplierCategoryId)
                .orElseThrow(() -> new RuntimeException("Supplier category not found"));
            
            Supplier supplier = Supplier.builder()
                .supplierName(supplierName)
                .supplierCategory(category)
                .phoneNumber(phoneNumber)
                .email(email)
                .note(note)
                .build();
            
            supplierService.updateSupplier(id, supplier);
            redirectAttributes.addFlashAttribute("successMessage", "Provider updated successfully!");
            return "redirect:/staff/supplier/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating provider: " + e.getMessage());
            return "redirect:/staff/supplier/edit/" + id;
        }
    }
}
