package service.sllbackend.web.mvc;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import service.sllbackend.entity.Voucher;
import service.sllbackend.entity.VoucherStatus;
import service.sllbackend.enumerator.DiscountType;
import service.sllbackend.repository.VoucherRepo;
import service.sllbackend.repository.VoucherStatusRepo;

@Controller
@RequestMapping("/staff/voucher")
public class StaffVoucherController {

    private final VoucherRepo voucherRepo;
    private final VoucherStatusRepo voucherStatusRepo;

    public StaffVoucherController(VoucherRepo voucherRepo, VoucherStatusRepo voucherStatusRepo) {
        this.voucherRepo = voucherRepo;
        this.voucherStatusRepo = voucherStatusRepo;
    }

    @GetMapping("/list")
    public String listVouchers(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String discountType,
            @RequestParam(required = false) Integer statusId,
            Model model) {
        
        // Get all statuses for filters
        List<VoucherStatus> allStatuses = voucherStatusRepo.findAll();
        model.addAttribute("allStatuses", allStatuses);
        
        // Get vouchers based on filters
        List<Voucher> vouchers;
        if ((code != null && !code.trim().isEmpty()) || 
            (name != null && !name.trim().isEmpty()) || 
            discountType != null || 
            statusId != null) {
            
            String searchCode = (code != null && !code.trim().isEmpty()) ? code.trim() : null;
            String searchName = (name != null && !name.trim().isEmpty()) ? name.trim() : null;
            DiscountType discType = (discountType != null && !discountType.isEmpty()) ? 
                DiscountType.valueOf(discountType) : null;
            
            vouchers = voucherRepo.searchVouchers(searchCode, searchName, discType, statusId);
        } else {
            vouchers = voucherRepo.findAllWithStatus();
        }
        
        model.addAttribute("vouchers", vouchers);
        model.addAttribute("searchCode", code);
        model.addAttribute("searchName", name);
        model.addAttribute("selectedDiscountType", discountType);
        model.addAttribute("selectedStatusId", statusId);
        
        return "staff-voucher-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        List<VoucherStatus> statuses = voucherStatusRepo.findAll();
        model.addAttribute("statuses", statuses);
        return "staff-voucher-create";
    }

    @PostMapping("/create")
    public String createVoucher(
            @RequestParam String voucherName,
            @RequestParam String voucherDescription,
            @RequestParam String voucherCode,
            @RequestParam String discountType,
            @RequestParam Integer discountAmount,
            @RequestParam String effectiveFrom,
            @RequestParam String effectiveTo,
            @RequestParam Integer maxUsage,
            @RequestParam Integer voucherStatusId,
            RedirectAttributes redirectAttributes) {
        
        try {
            VoucherStatus status = voucherStatusRepo.findById(voucherStatusId)
                .orElseThrow(() -> new RuntimeException("Voucher status not found"));
            
            Voucher voucher = Voucher.builder()
                .voucherName(voucherName)
                .voucherDescription(voucherDescription)
                .voucherCode(voucherCode)
                .discountType(DiscountType.valueOf(discountType))
                .discountAmount(discountAmount)
                .effectiveFrom(LocalDateTime.parse(effectiveFrom))
                .effectiveTo(LocalDateTime.parse(effectiveTo))
                .maxUsage(maxUsage)
                .usedCount(0)
                .voucherStatus(status)
                .build();
            
            voucherRepo.save(voucher);
            redirectAttributes.addFlashAttribute("successMessage", "Voucher created successfully!");
            return "redirect:/staff/voucher/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating voucher: " + e.getMessage());
            return "redirect:/staff/voucher/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Voucher voucher = voucherRepo.findByIdWithStatus(id)
            .orElseThrow(() -> new RuntimeException("Voucher not found"));
        
        List<VoucherStatus> statuses = voucherStatusRepo.findAll();
        model.addAttribute("voucher", voucher);
        model.addAttribute("statuses", statuses);
        return "staff-voucher-edit";
    }

    @PostMapping("/edit/{id}")
    public String editVoucher(
            @PathVariable Integer id,
            @RequestParam String voucherName,
            @RequestParam String voucherDescription,
            @RequestParam String voucherCode,
            @RequestParam String discountType,
            @RequestParam Integer discountAmount,
            @RequestParam String effectiveFrom,
            @RequestParam String effectiveTo,
            @RequestParam Integer maxUsage,
            @RequestParam Integer voucherStatusId,
            RedirectAttributes redirectAttributes) {
        
        try {
            Voucher voucher = voucherRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));
            
            VoucherStatus status = voucherStatusRepo.findById(voucherStatusId)
                .orElseThrow(() -> new RuntimeException("Voucher status not found"));
            
            voucher.setVoucherName(voucherName);
            voucher.setVoucherDescription(voucherDescription);
            voucher.setVoucherCode(voucherCode);
            voucher.setDiscountType(DiscountType.valueOf(discountType));
            voucher.setDiscountAmount(discountAmount);
            voucher.setEffectiveFrom(LocalDateTime.parse(effectiveFrom));
            voucher.setEffectiveTo(LocalDateTime.parse(effectiveTo));
            voucher.setMaxUsage(maxUsage);
            voucher.setVoucherStatus(status);
            
            voucherRepo.save(voucher);
            redirectAttributes.addFlashAttribute("successMessage", "Voucher updated successfully!");
            return "redirect:/staff/voucher/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating voucher: " + e.getMessage());
            return "redirect:/staff/voucher/edit/" + id;
        }
    }
}
